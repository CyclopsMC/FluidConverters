package org.cyclops.fluidconverters.tileentity;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.cyclops.cyclopscore.tileentity.CyclopsTileEntity;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;

/**
 * Tile Entity for {@link org.cyclops.fluidconverters.block.BlockFluidConverter}
 */
public class TileFluidConverter extends CyclopsTileEntity implements IFluidHandler {

    @Getter
    private FluidGroup fluidGroup;

    public TileFluidConverter() {
    }

    /**
     * Reads variable properties like fluidGroup from the nbt and
     * sets this tile entity to those values.
     * @param nbt nbt tag containing the state of the tile entity
     */
    public void readStateFromNBT(NBTTagCompound nbt) {
        String fluidGroupId = nbt.getString(BlockFluidConverter.NBTKEY_GROUPID);
        this.fluidGroup = fluidGroupId != null ? FluidGroupRegistry.getFluidGroupById(fluidGroupId) : null;
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
        if (fluidGroup != null) {
            tag.setString(BlockFluidConverter.NBTKEY_GROUPID, fluidGroup.getGroupId());
        }
    }
}
