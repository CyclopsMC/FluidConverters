package org.cyclops.fluidconverters.block

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import org.cyclops.fluidconverters.tileentity.TileEntityFluidConverter

object BlockFluidConverter extends BlockContainer(Material.circuits) {
    
    val NAMEDID = "FluidConverter"
    
    @Override
    def createNewTileEntity(world: World, meta: Int): TileEntity = {
        new TileEntityFluidConverter
    }
    
}