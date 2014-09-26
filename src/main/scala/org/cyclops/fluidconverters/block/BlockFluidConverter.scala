package org.cyclops.fluidconverters.block

import java.util

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.{World, Explosion, IBlockAccess}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidContainerRegistry
import org.cyclops.fluidconverters.{FluidColorAnalyzer, Reference}
import org.cyclops.fluidconverters.config.{FluidGroup, FluidGroupRegistry}
import org.cyclops.fluidconverters.tileentity.TileEntityFluidConverter

object BlockFluidConverter extends BlockContainer(Material.iron) {

    setBlockName(getUniqueName)
    setHardness(5F)
    
    final val NAMEDID = "FluidConverter"

    final val NBTKEY_GROUP = "fluidGroupId"
    final val NBTKEY_SIDE = "fluidSide%s"
    final val NBTKEY_UNITS = "units"

    private var NBT_CACHE : NBTTagCompound = null

    var COLOR_CACHE = 0

    private def getUniqueName : String = "blocks.fluidConverter"

    @SideOnly(Side.CLIENT)
    override def registerBlockIcons(iconRegister : IIconRegister) {
        blockIcon = iconRegister.registerIcon(getTextureName + "_open")
    }

    override def getTextureName : String = "%s:%s".format(Reference.MOD_ID, "fluidConverter")

    override def createNewTileEntity(world: World, meta: Int): TileEntity = new TileEntityFluidConverter

    override def isOpaqueCube : Boolean = false

    override def renderAsNormalBlock : Boolean = false

    def addGroupInfo(itemStack: ItemStack, fluidGroup: FluidGroup) {
        val groupId = fluidGroup.getGroupId
        var tag = itemStack.getTagCompound
        if(tag == null) tag = new NBTTagCompound
        tag.setString(NBTKEY_GROUP, groupId)
        itemStack.setTagCompound(tag)
    }

    @SideOnly(Side.CLIENT)
    override def getSubBlocks(item: Item, creativeTabs: CreativeTabs, list: java.util.List[_]) {
        val itemList = list.asInstanceOf[java.util.List[ItemStack]]
        for(fluidGroup <- FluidGroupRegistry.getGroups) {
            val itemStack = new ItemStack(this)
            addGroupInfo(itemStack, fluidGroup)
            itemList.add(itemStack)
        }
    }

    override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, stack: ItemStack) {
        if(world.getTileEntity(x, y, z) != null) {
            val tile = world.getTileEntity(x, y, z).asInstanceOf[TileEntityFluidConverter]
            if(stack.getTagCompound != null) {
                tile.readConverterDataFromNBT(stack.getTagCompound)
                tile.sendUpdate()
            }
        }
        super.onBlockPlacedBy(world, x, y, z, entity, stack)
    }

    override def onBlockActivated(world : World, x : Int, y : Int, z : Int, player : EntityPlayer, side : Int, xPos : Float, yPos : Float, zPos : Float): Boolean = {
        val item = player.inventory.getCurrentItem
        val tile = world.getTileEntity(x, y, z).asInstanceOf[TileEntityFluidConverter]
        if(item != null && FluidContainerRegistry.isFilledContainer(item)) {
            val fluid = FluidContainerRegistry.getFluidForFilledItem(item).getFluid
            if(tile.getFluidGroup != null && tile.getFluidGroup.getFluidElement(fluid) != null) {
                tile.setFluid(ForgeDirection.getOrientation(side), fluid)
            }
        } else {
            tile.setFluid(ForgeDirection.getOrientation(side), null)
        }
        true
    }

    private def onPreBlockDestroyed(world: World, x: Int, y: Int, z: Int) {
        val tile = world.getTileEntity(x, y, z)
        if(tile != null && tile.isInstanceOf[TileEntityFluidConverter]) {
            NBT_CACHE = tile.asInstanceOf[TileEntityFluidConverter].getNBTTagCompound
        } else {
            NBT_CACHE = null
        }
    }

    override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
        onPreBlockDestroyed(world, x, y, z)
        super.breakBlock(world, x, y, z, block, meta)
    }

    override def onBlockDestroyedByExplosion(world: World, x: Int, y: Int, z: Int, explosion: Explosion) {
        onPreBlockDestroyed(world, x, y, z)
        super.onBlockDestroyedByExplosion(world, x, y, z, explosion)
    }

    @SideOnly(Side.CLIENT)
    override def getRenderBlockPass: Int = 1

    @SideOnly(Side.CLIENT)
    override def colorMultiplier(world : IBlockAccess, x : Int, y : Int, z : Int): Int = {
        val tile = world.getTileEntity(x, y, z).asInstanceOf[TileEntityFluidConverter]
        FluidColorAnalyzer.getAverageColor(tile.getFluidGroup)
    }

    @SideOnly(Side.CLIENT)
    override def getRenderColor(meta : Int): Int = COLOR_CACHE

    override def getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): util.ArrayList[ItemStack] = {
        val drops = new util.ArrayList[ItemStack]()
        val itemStack = new ItemStack(getItemDropped(metadata, world.rand, fortune), 1, damageDropped(metadata))
        if(NBT_CACHE != null) {
            itemStack.setTagCompound(NBT_CACHE)
        }
        drops.add(itemStack)
        drops
    }
    
}