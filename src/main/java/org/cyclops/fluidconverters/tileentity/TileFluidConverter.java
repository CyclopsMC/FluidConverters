package org.cyclops.fluidconverters.tileentity;

import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.cyclopscore.persist.nbt.NBTPersist;
import org.cyclops.cyclopscore.tileentity.CyclopsTileEntity;
import org.cyclops.fluidconverters.block.BlockFluidConverterConfig;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupReference;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tile Entity for {@link org.cyclops.fluidconverters.block.BlockFluidConverter}
 */
public class TileFluidConverter extends CyclopsTileEntity implements CyclopsTileEntity.ITickingTile {

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
        for (EnumFacing side : EnumFacing.VALUES) {
            addCapabilitySided(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side, new FluidConverterCapability(this, side));
        }
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
     * @param fluidElement Fluid element that describes the fluid and its weight
     * @param amount The amount of the given fluid that needs to filled (in normalized units)
     * @param lossRatio Loss ratio on the amount that is filled
     * @param doFill Indicates if we should only simulate or not
     * @return The amount of fluid (in fluid units) that was filled into the handler
     */
    private int tryToFillFluid(IFluidHandler handler, FluidGroup.FluidElement fluidElement,
                               int amount, float lossRatio, boolean doFill) {
        // We can only drain from here if we have at least that amount in the buffer
        if (buffer <= 0) {
            return 0;
        }
        amount = Math.min(amount, (int) Math.floor(buffer));

        // Calculate the max amount of fluid (in fluid units) that will be passed to the output,
        // keeping loss into account
        int amountToBeFilled = MathHelper.floor(fluidElement.denormalize((1 - lossRatio) * amount));
        FluidStack fluidStack = new FluidStack(fluidElement.getFluid(), amountToBeFilled);

        // Simulate filling the handler
        int fluidAmountFilled = Math.max(0, handler.fill(fluidStack, false));
        // Fill up the actual handler
        if (doFill && fluidAmountFilled > 0) {
            fluidAmountFilled = Math.max(0, handler.fill(fluidStack, true));
            if (fluidAmountFilled > 0) {
                // Calculate the amount of fluid that is drained from the buffer
                float amountLost = fluidElement.normalize(fluidAmountFilled) / (1 - lossRatio);
                setBuffer(buffer - amountLost);
            }
        }

        return fluidAmountFilled;
    }

    public boolean fillSides(boolean simulate) {
        if (buffer <= 0) return false;

        // Loop over all possible output directions
        int filled = 0;
        for (Map.Entry<EnumFacing, Fluid> entry : fluidOutputs.entrySet()) {
            EnumFacing facing = entry.getKey();
            Fluid fluid = entry.getValue();
            FluidStack toFill = new FluidStack(fluid, Math.min(BlockFluidConverterConfig.mBRate, (int) Math.floor(buffer)));

            // Check if there is a fluid handler on this side
            IFluidHandler handler = TileHelpers.getCapability(world, getPos().offset(facing),
                    facing.getOpposite(), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

            // Try to fill fluid to this handler and update the buffer
            if (handler != null && handler.fill(toFill, false) > 0) {
                FluidGroup fluidGroup = getFluidGroup();
                filled += tryToFillFluid(handler, fluidGroup.getFluidElementByFluid(fluid), BlockFluidConverterConfig.mBRate, fluidGroup.getLossRatio(), !simulate);
                if (buffer <= 0) {
                    return filled > 0; // quit if there is nothing left to drain here
                }
            }
        }
        return filled > 0;
    }

    @Override
    protected void updateTileEntity() {
        if (!isValidConverter()) return;
        fillSides(false);
    }

    public static int getMaxBufferSize() {
        return BlockFluidConverterConfig.mBRate * 10;
    }

    private int addToBuffer(FluidGroup.FluidElement sourceFluidElement, int amount, boolean doFill) {
        if (buffer >= getMaxBufferSize()) {
            return 0;
        }
        // Save the liquid amount in the internal buffer only if we can fit all the fluid in the buffer
        float normalizedAmount = sourceFluidElement.normalize(amount);
        float newBufferSize = Math.min(buffer + normalizedAmount, getMaxBufferSize());
        float filled = newBufferSize - buffer;

        if (doFill) {
            setBuffer(newBufferSize);
        }

        return (int) filled;
    }

    private FluidStack doDrain(EnumFacing from, Fluid fluid, int amount, boolean doDrain) {
        if (!isValidConverter()) return null;

        // Is there actually anything to fill in that direction?
        IFluidHandler handler = TileHelpers.getCapability(world, getPos().offset(from),
                from.getOpposite(), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (handler == null) return null;

        // Try to fill it up
        FluidGroup fluidGroup = getFluidGroup();
        FluidGroup.FluidElement fluidElement = fluidGroup.getFluidElementByFluid(fluid);
        int liquidDrained = tryToFillFluid(handler, fluidElement, amount, fluidGroup.getLossRatio(), doDrain);

        return new FluidStack(fluid, liquidDrained);
    }

    public static class FluidConverterCapability implements IFluidHandler {

        private final TileFluidConverter fluidConverter;
        private final EnumFacing side;

        public FluidConverterCapability(TileFluidConverter fluidConverter, EnumFacing side) {
            this.fluidConverter = fluidConverter;
            this.side = side;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[] { new FluidTankProperties(null, 0)};
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (!fluidConverter.isValidConverter()
                    || resource == null
                    || fluidConverter.getFluidGroup().getFluidElementByFluid(resource.getFluid()) == null
                    || fluidConverter.fluidOutputs.get(side) == null) return 0;

            // Fetch the fluid element from the source
            FluidGroup.FluidElement sourceFluidElement = fluidConverter.getFluidGroup().getFluidElementByFluid(resource.getFluid());
            if (sourceFluidElement == null) return 0;

            // Save all liquid in the buffer
            return fluidConverter.addToBuffer(sourceFluidElement, resource.amount, doFill);
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (resource == null || resource.getFluid().equals(fluidConverter.fluidOutputs.get(side))) return null;

            Fluid fluid = fluidConverter.fluidOutputs.get(side);
            return (fluid != null && resource.getFluid().equals(fluid)) ?
                    fluidConverter.doDrain(side, fluid, resource.amount, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            Fluid fluid = fluidConverter.fluidOutputs.get(side);
            return fluid == null ? null : fluidConverter.doDrain(side, fluid, maxDrain, doDrain);
        }
    }

}
