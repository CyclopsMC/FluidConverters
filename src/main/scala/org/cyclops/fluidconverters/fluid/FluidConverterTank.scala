package org.cyclops.fluidconverters.fluid

import net.minecraft.tileentity.TileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

class FluidConverterTank(name: String, capacity: Int, tile: TileEntity)
	extends Tank(name, capacity, tile) {
    
    /**
     * The NBT name for the fluid tank.
     */
    final val NBT_FLUID_GROUP = "fluidGroup";
    
    private var fluidGroup : Fluid = null;
    
    /**
     * Get the accepted fluid for this tank.
     * @return The accepted fluid.
     */
    def getFluidGroup(): Fluid = {
        fluidGroup
    }

    override def writeTankToNBT(nbt: NBTTagCompound) {
        super.writeTankToNBT(nbt);
        if(fluidGroup != null) {
            nbt.setString(NBT_FLUID_GROUP, fluidGroup.getName());
        }
    }

    override def readTankFromNBT(nbt: NBTTagCompound) {
        super.readTankFromNBT(nbt);
        fluidGroup = FluidRegistry.getFluid(nbt.getString(NBT_FLUID_GROUP));
    }
    
    /**
     * If this tank can accept the given fluid.
     * @param fluid The fluid that needs to be checked.
     * @return If this tank can accept it.
     */
    def canTankAccept(fluid: Fluid): Boolean = {
        // TODO: check if in group
    	getFluidGroup.equals(fluid)
    }

    override def fill(resource: FluidStack, doFill: Boolean): Int = {
        // TODO
    	//var converted = converter.convert(resource);
        return super.fill(resource, doFill);
    }
    
}