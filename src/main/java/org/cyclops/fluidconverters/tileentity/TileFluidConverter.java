package org.cyclops.fluidconverters.tileentity;

import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.cyclopscore.persist.nbt.NBTPersist;
import org.cyclops.cyclopscore.tileentity.CyclopsTileEntity;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupReference;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tile Entity for {@link org.cyclops.fluidconverters.block.BlockFluidConverter}
 */
public class TileFluidConverter extends CyclopsTileEntity implements IFluidHandler, CyclopsTileEntity.ITickingTile {

    // Maximum size of the internal buffer
    public static final int MAX_BUFFER_SIZE = 1000;
    // Rate at which we output millibuckets each tick
    private static final int MBRATE = 10;

    @NBTPersist
    @Getter
    private FluidGroupReference fluidGroupRef;
    // NOTE: Value should be same as the name of the field above
    public static final String NBT_FLUID_GROUP_REF = "fluidGroupRef";

    @NBTPersist
    @Getter
    private Map<EnumFacing, Fluid> fluidOutputs = new TreeMap<EnumFacing, Fluid>();
    // NOTE: Value should be same as the name of the field above
    public static final String NBT_FLUID_OUTPUTS = "fluidOutputs";

    @NBTPersist
    @Getter
    // Internal buffer that keeps liquid as normalized units
    private float buffer;
    // NOTE: Value should be same as the name of the field above
    public static final String NBT_BUFFER = "buffer";

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    public TileFluidConverter() {
    }

    public boolean isValidConverter() {
        return getFluidGroup() != null && getFluidOutputs() != null;
    }

    public FluidGroup getFluidGroup() {
        return fluidGroupRef != null ? fluidGroupRef.getFluidGroup() : null;
    }

    public void setBuffer(float newBuffer) {
        if (this.buffer != newBuffer) {
            this.buffer = newBuffer;

            // Force an explicit update when setting the buffer
            this.sendUpdate();
        }
    }

    /**
     * Binds a given fluid to a given side, so only that fluid can be outputted from that side
     * of the block.
     * @param facing The side of the block.
     * @param fluid The fluid that needs to be bound.
     * @return Returns true if the fluid output for the given side changed;
     */
    public boolean setFluidOutput(EnumFacing facing, Fluid fluid) {
        if (!isValidConverter()) return false;

        if (fluid == null) {
            fluidOutputs.remove(facing);
            return true;
        }

        boolean changed = false;
        FluidGroup.FluidElement fluidElement = getFluidGroup().getFluidElementByFluid(fluid);
        if (fluidElement != null) {
            changed = !fluid.equals(fluidOutputs.get(facing));
            if (changed) {
                fluidOutputs.put(facing, fluid);
            }
        }

        return changed;
    }

    /**
     * Tries to fill the given handler with a given amount of fluid, specified in normalized units.
     * @param handler Destination for the fluid
     * @param from The facing on the block from which the fluid will be pushed
     * @param fluidElement Fluid element that describes the fluid and its weight
     * @param amount The amount of the given fluid that needs to filled (in normalized units)
     * @param doFill Indicates if we should only simulate or not
     * @return The amount of fluid (in fluid units) that was filled into the handler
     */
    private int tryToFillFluid(IFluidHandler handler, EnumFacing from, FluidGroup.FluidElement fluidElement, int amount, boolean doFill) {
        // We can only drain from here if we have at least that amount in the buffer
        if (buffer < amount) return 0;

        int amountToBeFilled = MathHelper.floor_float(fluidElement.denormalize(amount));
        FluidStack fluidStack = new FluidStack(fluidElement.getFluid(), amountToBeFilled);

        // Simulate filling the handler
        int fluidAmountFilled = Math.max(0, handler.fill(from, fluidStack, false));
        // Fill up the actual handler
        if (doFill && fluidAmountFilled > 0) {
            fluidAmountFilled = Math.max(0, handler.fill(from, fluidStack, true));
            setBuffer(buffer - fluidElement.normalize(fluidAmountFilled));
        }

        return fluidAmountFilled;
    }

    @Override
    protected void updateTileEntity() {
        if (!isValidConverter()) return;
        if (buffer < MBRATE) return;

        // Loop over all possible output directions
        for (Map.Entry<EnumFacing, Fluid> entry : fluidOutputs.entrySet()) {
            EnumFacing facing = entry.getKey();
            Fluid fluid = entry.getValue();

            // Check if there is a fluid handler on this side
            IFluidHandler handler = TileHelpers.getSafeTile(worldObj, getPos().offset(facing), IFluidHandler.class);
            EnumFacing fillSide = facing.getOpposite();

            // Try to fill fluid to this handler and update the buffer
            if (handler != null && handler.canFill(fillSide, fluid)) {
                tryToFillFluid(handler, fillSide, getFluidGroup().getFluidElementByFluid(fluid), MBRATE, true);
                if (buffer < MBRATE) return;    // quit if there is nothing left to drain here
            }
        }
    }

    private boolean addToBuffer(FluidGroup.FluidElement sourceFluidElement, int amount, boolean doFill) {
        // Save the liquid amount in the internal buffer only if we can fit all the fluid in the buffer
        float normalizedAmount = sourceFluidElement.normalize(amount);
        float maxBufferSize = sourceFluidElement.normalize(MAX_BUFFER_SIZE);
        float newBufferSize = buffer + normalizedAmount;
        boolean canFill = newBufferSize <= maxBufferSize;

        if (doFill && canFill) {
            setBuffer(newBufferSize);
        }

        return canFill;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (!isValidConverter()) return 0;

        // Fetch the fluid element from the source
        FluidGroup.FluidElement sourceFluidElement = getFluidGroup().getFluidElementByFluid(resource.getFluid());
        if (sourceFluidElement == null) return 0;

        // Save all liquid in the buffer
        boolean addedToBuffer = addToBuffer(sourceFluidElement, resource.amount, doFill);

        return addedToBuffer ? resource.amount : 0;
    }

    private FluidStack doDrain(EnumFacing from, Fluid fluid, int amount, boolean doDrain) {
        if (!isValidConverter()) return null;

        // Is there actually anything to fill in that direction?
        IFluidHandler handler = TileHelpers.getSafeTile(worldObj, getPos().offset(from), IFluidHandler.class);
        if (handler == null) return null;

        // Try to fill it up
        FluidGroup.FluidElement fluidElement = getFluidGroup().getFluidElementByFluid(fluid);
        int liquidDrained = tryToFillFluid(handler, from.getOpposite(), fluidElement, amount, doDrain);

        return new FluidStack(fluid, liquidDrained);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean simulate) {
        Fluid fluid = fluidOutputs.get(from);
        return (fluid != null && resource.getFluid().equals(fluid)) ?
            doDrain(from, fluid, resource.amount, simulate) : null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean simulate) {
        Fluid fluid = fluidOutputs.get(from);
        return fluid == null ? null : doDrain(from, fluid, maxDrain, simulate);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return isValidConverter() && getFluidGroup().getFluidElementByFluid(fluid) != null;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return fluid != null && fluid.equals(fluidOutputs.get(from));
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[0];
    }

}
