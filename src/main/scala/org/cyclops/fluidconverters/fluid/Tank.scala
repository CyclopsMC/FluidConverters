package org.cyclops.fluidconverters.fluid

import net.minecraftforge.fluids.FluidTank
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.Fluid
import net.minecraft.nbt.NBTTagCompound

/**
 * A simple fluid tank.
 * @author rubensworks
 *
 */
abstract class Tank(name: String, capacity: Int, tile: TileEntity) extends FluidTank(capacity) {
    
    /**
     * Check if this tank is empty.
     * @return If the tank is empty; no fluid is inside of it.
     */
    def isEmpty(): Boolean = {
        getFluid() == null || getFluid().amount <= 0
    }

    /**
     * Check if this tank is full; the capacity is reached.
     * @return If this tank is full.
     */
    def isFull(): Boolean = {
        getFluid() != null && getFluid().amount >= getCapacity()
    }

    /**
     * Get the fluid that currently occupies this tank, will return null if there is no fluid.
     * @return The inner fluid.
     */
    def getFluidType(): Fluid = {
        if(getFluid() != null) {
            return getFluid().getFluid()
        }
        null
    }

    final override def writeToNBT(nbt: NBTTagCompound): NBTTagCompound = {
        var tankData = new NBTTagCompound;
        super.writeToNBT(tankData);
        writeTankToNBT(tankData);
        nbt.setTag(name, tankData);
        nbt
    }

    final override def readFromNBT(nbt: NBTTagCompound): FluidTank = {
        if(nbt.hasKey(name)) {
            var tankData = nbt.getCompoundTag(name);
            super.readFromNBT(tankData);
            readTankFromNBT(tankData);
        }
        this
    }

    /**
     * Write the tank contents to NBT.
     * @param nbt The NBT tag to write to.
     */
    def writeTankToNBT(nbt: NBTTagCompound) {}

    /**
     * Read the tank contents from NBT.
     * @param nbt The NBT tag to write from.
     */
    def readTankFromNBT(nbt: NBTTagCompound) {}
    
}