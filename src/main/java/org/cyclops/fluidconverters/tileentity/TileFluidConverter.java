package org.cyclops.fluidconverters.tileentity;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.cyclopscore.tileentity.CyclopsTileEntity;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Tile Entity for {@link org.cyclops.fluidconverters.block.BlockFluidConverter}
 */
public class TileFluidConverter extends CyclopsTileEntity implements IFluidHandler, CyclopsTileEntity.ITickingTile {

    @Getter
    private FluidGroup fluidGroup;
    @Getter
    private Map<EnumFacing, FluidGroup.FluidElement> fluidOutputs = new TreeMap<EnumFacing, FluidGroup.FluidElement>();

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    public TileFluidConverter() {
    }

    /**
     * Reads variable properties like fluidGroup from the nbt and
     * sets this tile entity to those values.
     * @param nbt nbt tag containing the state of the tile entity
     */
    public void readStateFromNBT(NBTTagCompound nbt) {
        // Fluid group
        this.fluidGroup = BlockFluidConverter.getFluidGroupFromNBT(nbt);

        // Fluid outputs
        Map<EnumFacing, Fluid> fluidMap = BlockFluidConverter.getFluidOutputsFromNBT(nbt);
        for (Map.Entry<EnumFacing, Fluid> entry : fluidMap.entrySet()) {
            EnumFacing facing = entry.getKey();
            Fluid fluid = entry.getValue();

            FluidGroup.FluidElement fluidElement = (fluidGroup != null && fluid != null) ?
                    fluidGroup.getFluidElementByFluid(fluid) : null;
            if (fluidElement != null) fluidOutputs.put(facing, fluidElement);
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
        if (fluid == null) {
            fluidOutputs.remove(facing);
            return true;
        }

        boolean changed = false;
        FluidGroup.FluidElement fluidElement = fluidGroup.getFluidElementByFluid(fluid);
        if (fluidElement != null) {
            changed = !fluidElement.equals(fluidOutputs.get(facing));
            if (changed) {
                fluidOutputs.put(facing, fluidElement);
            }
        }

        return changed;
    }

    /**
     * Finds all possible destination fluid handlers that neighbour this block, together with the side
     * the destination is facing relative to the current block.
     * @return A queue of (facing, destination fluid handler) pairs.
     */
    private Queue<Pair<IFluidHandler, EnumFacing>> getDestinations() {
        Queue<Pair<IFluidHandler, EnumFacing>> destinations = Lists.newLinkedList();
        for (Map.Entry<EnumFacing, FluidGroup.FluidElement> entry : fluidOutputs.entrySet()) {
            EnumFacing facing = entry.getKey();
            FluidGroup.FluidElement fluidElement = entry.getValue();

            // Fetch the handler on this side
            IFluidHandler handler = TileHelpers.getSafeTile(worldObj, getPos().offset(facing), IFluidHandler.class);

            // Check if it can be filled from the opposite side with the given fluid
            if (handler != null && handler.canFill(facing.getOpposite(), fluidElement.getFluid()))
                destinations.add(Pair.of(handler, facing));
        }
        return destinations;
    }

    private float fillDestinations(Queue<Pair<IFluidHandler, EnumFacing>> destinations, int unitsPerOutput, boolean doFill) {
        float totalFilled = 0;
        for (Pair<IFluidHandler, EnumFacing> pair : destinations) {
            IFluidHandler dest = pair.getKey();
            EnumFacing sourceSide = pair.getValue();
            EnumFacing destSide = sourceSide.getOpposite();

            FluidGroup.FluidElement fluidElement = fluidOutputs.get(sourceSide);
            float destWeight = fluidElement.getValue();

            // Convert the units this output receives to "liquid units"
            // and floor because we can't use more units than are given
            FluidStack fluidStack = new FluidStack(
                    fluidElement.getFluid(),
                    MathHelper.floor_float(unitsPerOutput * destWeight)
            );

            // Convert the amount filled back from "liquid units" to units
            totalFilled += dest.fill(destSide, fluidStack, doFill) / destWeight;
        }
        return totalFilled;
    }

    /**
     * Calculates the max number of units each output can receive.
     * @param resourceAmount The amount of units that is offered by a resource
     * @param sourceWeight The weight of the source in units
     * @param destinationSize The number of outputs over which we need spread the resourceAmount
     * @return The maximum amount of units every output may drain.
     */
    private int calculateMaxUnitsPerOutput(int resourceAmount, float sourceWeight, int destinationSize) {
        return MathHelper.floor_float(resourceAmount / (sourceWeight * destinationSize));
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        IFluidHandler source = TileHelpers.getSafeTile(worldObj, getPos().offset(from), IFluidHandler.class);

        // Fetch the weight of the fluid in the source
        FluidGroup.FluidElement sourceFluidElement = fluidGroup.getFluidElementByFluid(resource.getFluid());
        if (sourceFluidElement == null) return 0;
        float sourceWeight = sourceFluidElement.getValue();

        // Fetch all possible destinations
        Queue<Pair<IFluidHandler, EnumFacing>> destinations = getDestinations();
        int destinationSize = destinations.size();
        if (destinationSize == 0) return 0;

        // Remove possible outputs until we are able to split the resource's liquid over all outputs
        int unitsPerOutput = calculateMaxUnitsPerOutput(resource.amount, sourceWeight, destinationSize);
        while (unitsPerOutput == 0 && !destinations.isEmpty()) {
            destinations.remove();
            unitsPerOutput = calculateMaxUnitsPerOutput(resource.amount, sourceWeight, destinations.size());
        }
        if (destinations.isEmpty()) return 0;

        // Fill each destination
        float totalFilled = fillDestinations(destinations, unitsPerOutput, doFill);

        // Convert the total amount drained back to "source liquid units"
        // Ceil: If we used e.g. 3.1 mb, we have used more than 3, so 4 mb
        return MathHelper.ceiling_float_int(totalFilled * sourceWeight);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluidGroup.getFluidElementByFluid(fluid) != null;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[0];
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readStateFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        // fluid group
        if (fluidGroup != null) {
            tag.setString(BlockFluidConverter.NBTKEY_GROUPID, fluidGroup.getGroupId());
        }

        // fluid outputs
        for (Map.Entry<EnumFacing, FluidGroup.FluidElement> entry : fluidOutputs.entrySet())
            tag.setString(BlockFluidConverter.NBT_KEY_FLUIDSIDE(entry.getKey()), entry.getValue().getFluid().getName());
    }
}
