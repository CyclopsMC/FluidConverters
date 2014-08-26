package org.cyclops.fluidconverters.block

import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.util.{StatCollector, EnumChatFormatting}
import org.cyclops.fluidconverters.FluidColorAnalyzer
import org.cyclops.fluidconverters.config.{FluidGroup, FluidGroupRegistry}
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11

/**
 * Specific item block for the BlockFluidConverter.
 * @author rubensworks
 */
class ItemBlockFluidConverter(block: Block) extends ItemBlock(block) {

    private def getFluidGroup(itemStack: ItemStack): FluidGroup = {
        if(itemStack.getTagCompound != null) {
            val fluidGroupId = itemStack.getTagCompound.getString(BlockFluidConverter.NBTKEY_GROUP)
            return FluidGroupRegistry.getGroup(fluidGroupId)
        }
        null
    }

    override def addInformation(itemStack: ItemStack, entityPlayer: EntityPlayer, list: java.util.List[_],
                                par4: Boolean) {
        val infoList = list.asInstanceOf[java.util.List[String]]
        var validContent = false
        val group = getFluidGroup(itemStack)
        if (group != null) {
            infoList.add("%s%s: %s".format(EnumChatFormatting.GOLD,
                StatCollector.translateToLocal("tile.blocks.fluidConverter.converter"), group.getGroupName))
            validContent = true
            if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                for (element <- group.getFluidElements) {
                    if(element.getFluid != null) {
                        infoList.add("%s%s: %.2f".format(EnumChatFormatting.GRAY, element.getFluid.getLocalizedName, element.getValue))
                    }
                }
            } else {
                infoList.add("%s%s%s".format(EnumChatFormatting.GRAY, EnumChatFormatting.ITALIC,
                    StatCollector.translateToLocal("info.moreInfo")))
            }
        }
        if(!validContent) {
            infoList.add("%s%s".format(EnumChatFormatting.ITALIC, StatCollector.translateToLocal("info.invalid")))
        }
    }

    @SideOnly(Side.CLIENT)
    override def requiresMultipleRenderPasses: Boolean = true

    @SideOnly(Side.CLIENT)
    override def getColorFromItemStack(itemStack : ItemStack, renderPass : Int): Int = {
        if(renderPass == 1) {
            val group = getFluidGroup(itemStack)
            if (group != null) {
                return FluidColorAnalyzer.getAverageColor(group)
            }
        }
        super.getColorFromItemStack(itemStack, renderPass)
    }

}
