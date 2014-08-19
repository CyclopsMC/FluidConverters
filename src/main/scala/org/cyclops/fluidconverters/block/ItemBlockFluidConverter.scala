package org.cyclops.fluidconverters.block

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.util.EnumChatFormatting
import org.cyclops.fluidconverters.config.{FluidGroup, FluidGroupRegistry}

/**
 * Specific item block for the BlockFluidConverter.
 * @author rubensworks
 */
class ItemBlockFluidConverter(block: Block) extends ItemBlock(block) {

    override def addInformation(itemStack: ItemStack, entityPlayer: EntityPlayer, list: java.util.List[_],
                                par4: Boolean) {
        val infoList = list.asInstanceOf[java.util.List[String]]
        infoList.add("%s%sFluids:".format(EnumChatFormatting.GOLD, EnumChatFormatting.BOLD))
        var validContent = false
        if(itemStack.getTagCompound != null) {
            val fluidGroupId = itemStack.getTagCompound.getString(BlockFluidConverter.NBTKEY_GROUP)
            if(fluidGroupId != null) {
                val group = FluidGroupRegistry.getGroup(fluidGroupId)
                validContent = true
                for(element <- group.getFluidElements) {
                    infoList.add("%s    %s : %f".format(EnumChatFormatting.AQUA, element.getFluidName, element.getValue))
                }
            }
        }
        if(!validContent) {
            infoList.add("%s    None".format(EnumChatFormatting.ITALIC))
        }

    }

}
