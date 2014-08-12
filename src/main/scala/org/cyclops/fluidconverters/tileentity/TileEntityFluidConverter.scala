package org.cyclops.fluidconverters.tileentity

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.IFluidHandler
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.network.Packet
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.Fluid
import org.cyclops.fluidconverters.fluid.FluidConverterTank

class TileEntityFluidConverter extends TileEntity with IFluidHandler {
	
    private val tank : FluidConverterTank = new FluidConverterTank("FluidConverterTank", 1000, this);
    
    /**
     * Send a world update for the coordinates of this tile entity.
     */
    def sendUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    override def getDescriptionPacket(): Packet = {
        new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, getNBTTagCompound())
    }

    override def onDataPacket(net: NetworkManager, packet: S35PacketUpdateTileEntity) {
        super.onDataPacket(net, packet);
        val tag = packet.func_148857_g;
        readFromNBT(tag);
        onUpdateReceived();
    }

    override def writeToNBT(tag: NBTTagCompound) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
    }

    override def readFromNBT(tag: NBTTagCompound) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }
    
    /**
     * Get the NBT tag for this tile entity.
     * @return The NBT tag that is created with the
     * {@link EvilCraftTileEntity#writeToNBT(NBTTagCompound)} method.
     */
    def getNBTTagCompound(): NBTTagCompound = {
        val tag = new NBTTagCompound();
        writeToNBT(tag);
        tag
    }
    
    /**
     * This method is called when the tile entity receives
     * an update (ie a data packet) from the server. 
     * If this tile entity uses NBT, then the NBT will have
     * already been updated when this method is called.
     */
    def onUpdateReceived() {
        
    }

    override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int = {
        tank.fill(resource, doFill)
    }

    override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean): FluidStack = {
        if(resource == null || !resource.isFluidEqual(tank.getFluid))
            return null;
        drain(from, resource.amount, doDrain)
    }

    override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean): FluidStack = {
        tank.drain(maxDrain, doDrain)
    }

    override def canFill(from: ForgeDirection, fluid: Fluid): Boolean = {
        tank.getFluidGroup == null || tank.canTankAccept(fluid)
    }

    override def canDrain(from: ForgeDirection, fluid: Fluid): Boolean = {
        tank.getFluidGroup == null || tank.canTankAccept(fluid)
    }

    override def getTankInfo(from: ForgeDirection): Array[FluidTankInfo] = {
        val info = new Array[FluidTankInfo](1);
        info(0) = tank.getInfo;
        info
    }
    
}