package org.cyclops.fluidconverters

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import org.cyclops.fluidconverters.block.BlockFluidConverter

object FluidConvertersTab extends CreativeTabs(Reference.MOD_NAME) {

    @Override
    def getTabIconItem(): Item = {
        Item.getItemFromBlock(BlockFluidConverter)
    }
    
}