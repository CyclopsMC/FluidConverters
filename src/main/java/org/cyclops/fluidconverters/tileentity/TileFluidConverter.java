package org.cyclops.fluidconverters.tileentity;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;
import org.cyclops.cyclopscore.tileentity.CyclopsTileEntity;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tile Entity for {@link org.cyclops.fluidconverters.block.BlockFluidConverter}
 */
public class TileFluidConverter extends CyclopsTileEntity implements IFluidHandler {

    @Getter
    private FluidGroup fluidGroup;
    @Getter
    private Map<EnumFacing, Fluid> fluidOutputs = new TreeMap<EnumFacing, Fluid>();

    public TileFluidConverter() {
    }

    /**
     * Reads variable properties like fluidGroup from the nbt and
     * sets this tile entity to those values.
     * @param nbt nbt tag containing the state of the tile entity
     */
    public void readStateFromNBT(NBTTagCompound nbt) {
        // Fluid group
        String fluidGroupId = nbt.getString(BlockFluidConverter.NBTKEY_GROUPID);
        this.fluidGroup = fluidGroupId != null ? FluidGroupRegistry.getFluidGroupById(fluidGroupId) : null;

        // Fluid outputs
        for (EnumFacing facing : EnumFacing.values()) {
            String fluidName = nbt.getString(BlockFluidConverter.NBT_KEY_FLUIDSIDE(facing));
            Fluid fluid = fluidName != null ? FluidRegistry.getFluid(fluidName) : null;
            if (fluid != null) fluidOutputs.put(facing, fluid);
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
        if (fluidGroup.getFluidElementByFluid(fluid) != null) {
            changed = !fluid.equals(fluidOutputs.get(facing));
            if (changed) {
                fluidOutputs.put(facing, fluid);
            }
        }

        return changed;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return 0;
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
        return false;
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
        for (Map.Entry<EnumFacing, Fluid> entry : fluidOutputs.entrySet())
            tag.setString(BlockFluidConverter.NBT_KEY_FLUIDSIDE(entry.getKey()), entry.getValue().getName());
    }
}
