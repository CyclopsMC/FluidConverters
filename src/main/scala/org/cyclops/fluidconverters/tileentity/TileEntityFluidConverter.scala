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
import org.cyclops.fluidconverters.block.BlockFluidConverter
import org.cyclops.fluidconverters.config.{FluidGroup, FluidGroupRegistry, FluidElement}

class TileEntityFluidConverter extends TileEntity with IFluidHandler {

    private final val CAPACITY = 1000

    private var fluidGroupId : String = null
    private val fluidSides : Array[String] = new Array[String](ForgeDirection.VALID_DIRECTIONS.size)
    private var units = 0; // Used as a general fluid amount, that can be converted into group fluids.
    
    /**
     * Send a world update for the coordinates of this tile entity.
     */
    def sendUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
    }

    override def getDescriptionPacket: Packet = {
        new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, getNBTTagCompound())
    }

    override def onDataPacket(net: NetworkManager, packet: S35PacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        val tag = packet.func_148857_g
        readFromNBT(tag)
        onUpdateReceived()
    }

    override def writeToNBT(tag: NBTTagCompound) {
        super.writeToNBT(tag)
        tag.setString(BlockFluidConverter.NBTKEY_GROUP, fluidGroupId)
        for(i <- fluidSides.indices) {
            tag.setString(BlockFluidConverter.NBTKEY_SIDE.format(i), fluidSides(i))
        }
        tag.setInteger(BlockFluidConverter.NBTKEY_UNITS, units)
    }

    override def readFromNBT(tag: NBTTagCompound) {
        super.readFromNBT(tag)
        fluidGroupId = tag.getString(BlockFluidConverter.NBTKEY_GROUP)
        for(i <- fluidSides.indices) {
            fluidSides(i) = tag.getString(BlockFluidConverter.NBTKEY_SIDE.format(i))
        }
        units = tag.getInteger(BlockFluidConverter.NBTKEY_UNITS)
    }
    
    /**
     * Get the NBT tag for this tile entity.
     * @return The NBT tag that is created.
     */
    def getNBTTagCompound: NBTTagCompound = {
        val tag = new NBTTagCompound()
        writeToNBT(tag)
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

    private def getFluidGroup : FluidGroup = {
        FluidGroupRegistry.getGroup(fluidGroupId)
    }

    private def getFluidElement(side : ForgeDirection) : FluidElement =  {
        val fluidName = fluidSides(side.ordinal())
        if(fluidName == null) null
        getFluidGroup().getFluidElement(fluidName)
    }

    override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int = {
        if(getFluidGroup() == null) return 0
        val fluidElement = getFluidGroup().getFluidElement(resource.getFluid)
        if(fluidElement == null) return 0

        val addUnits = (resource.amount * fluidElement.getCost).toInt
        var addedAmount = 0
        if(units + addUnits <= CAPACITY) {
            addedAmount = resource.amount
            if(doFill) units += addUnits.toInt

        } else {
            addedAmount = ((CAPACITY - units) / fluidElement.getCost).toInt
            if(doFill) units = CAPACITY
        }

        addedAmount
    }

    override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean): FluidStack = {
        if(resource == null) return null
        drain(from, resource.amount, doDrain)
    }

    override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean): FluidStack = {
        val fluidElement = getFluidElement(from)
        if(fluidElement == null) return null

        val toDrain = Math.min(maxDrain, units * fluidElement.getCost)
        if(doDrain) units -= (toDrain / fluidElement.getCost).toInt
        new FluidStack(fluidElement.getFluid, toDrain.toInt)
    }

    override def canFill(from: ForgeDirection, fluid: Fluid): Boolean = {
        fill(from, new FluidStack(fluid, 1), false) > 0
    }

    override def canDrain(from: ForgeDirection, fluid: Fluid): Boolean = {
        val fluidStack = drain(from, 1, false)
        fluidStack != null && fluidStack.amount > 0
    }

    override def getTankInfo(from: ForgeDirection): Array[FluidTankInfo] = {
        val fluidGroup = getFluidGroup()
        if(fluidGroup == null) {
            return new Array[FluidTankInfo](0)
        }
        val info = new Array[FluidTankInfo](fluidGroup.getFluidElements.length)
        for(i <- fluidGroup.getFluidElements.indices) {
            val element = fluidGroup.getFluidElements()(i)
            info(i) = new FluidTankInfo(new FluidStack(element.getFluid, (units / element.getCost).toInt), CAPACITY)
        }
        info
    }
    
}