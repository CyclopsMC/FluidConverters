package org.cyclops.fluidconverters.block

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.util.{StatCollector, EnumChatFormatting}
import org.cyclops.fluidconverters.config.{FluidGroup, FluidGroupRegistry}
import org.lwjgl.input.Keyboard

/**
 * Specific item block for the BlockFluidConverter.
 * @author rubensworks
 */
class ItemBlockFluidConverter(block: Block) extends ItemBlock(block) {

    override def addInformation(itemStack: ItemStack, entityPlayer: EntityPlayer, list: java.util.List[_],
                                par4: Boolean) {
        val infoList = list.asInstanceOf[java.util.List[String]]
        var validContent = false
        if(itemStack.getTagCompound != null) {
            val fluidGroupId = itemStack.getTagCompound.getString(BlockFluidConverter.NBTKEY_GROUP)
            val group = FluidGroupRegistry.getGroup(fluidGroupId)
            if(group != null) {
                infoList.add("%s%s: %s".format(EnumChatFormatting.GOLD,
                    StatCollector.translateToLocal("tile.blocks.fluidConverter.converter"), group.getGroupName))
                validContent = true
                if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    for (element <- group.getFluidElements) {
                        infoList.add("%s%s: %.2f".format(EnumChatFormatting.GRAY, element.getFluid.getLocalizedName, element.getValue))
                    }
                } else {
                    infoList.add("%s%s%s".format(EnumChatFormatting.GRAY, EnumChatFormatting.ITALIC,
                        StatCollector.translateToLocal("info.moreInfo")))
                }
            }
        }
        if(!validContent) {
            infoList.add("%s%s".format(EnumChatFormatting.ITALIC, StatCollector.translateToLocal("info.invalid")))
        }

    }

}
