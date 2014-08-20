package org.cyclops.fluidconverters

import cpw.mods.fml.client.registry.ClientRegistry
import org.cyclops.fluidconverters.render.RenderFluidConverter
import org.cyclops.fluidconverters.tileentity.TileEntityFluidConverter

/**
 * Client proxy.
 * @author rubensworks
 */
class ClientProxy extends CommonProxy {

    override def registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileEntityFluidConverter], RenderFluidConverter)
    }

}
