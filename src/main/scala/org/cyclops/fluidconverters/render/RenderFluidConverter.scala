package org.cyclops.fluidconverters.render

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.{RenderItem, RenderManager}
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection
import org.cyclops.fluidconverters.tileentity.TileEntityFluidConverter
import org.lwjgl.opengl.GL11

/**
 * Renderer for the fluid sides of the fluid converter.
 * @author rubensworks
 */
object RenderFluidConverter extends TileEntitySpecialRenderer {

    val coordinates = Array(
        Array( // DOWN
            Array(0.01D, 0.01D, 0.01D),
            Array(0.01D, 0.01D, 0.99D),
            Array(0.99D, 0.01D, 0.99D),
            Array(0.99D, 0.01D, 0.01D)
        ),
        Array( // UP
            Array(0.01D, 0.99D, 0.01D),
            Array(0.01D, 0.99D, 0.99D),
            Array(0.99D, 0.99D, 0.99D),
            Array(0.99D, 0.99D, 0.01D)
        ),
        Array( // NORTH
            Array(0.01D, 0.01D, 0.01D),
            Array(0.01D, 0.99D, 0.01D),
            Array(0.99D, 0.99D, 0.01D),
            Array(0.99D, 0.01D, 0.01D)
        ),
        Array( // SOUTH
            Array(0.01D, 0.01D, 0.99D),
            Array(0.01D, 0.99D, 0.99D),
            Array(0.99D, 0.99D, 0.99D),
            Array(0.99D, 0.01D, 0.99D)
        ),
        Array( // WEST
            Array(0.01D, 0.01D, 0.01D),
            Array(0.01D, 0.99D, 0.01D),
            Array(0.01D, 0.99D, 0.99D),
            Array(0.01D, 0.01D, 0.99D)
        ),
        Array( // EAST
            Array(0.99D, 0.01D, 0.01D),
            Array(0.99D, 0.99D, 0.01D),
            Array(0.99D, 0.99D, 0.99D),
            Array(0.99D, 0.01D, 0.99D)
        )
    )

    def renderTileEntityAt(tile : TileEntity, x : Double, y : Double, z : Double, partialTickTime : Float) {
        val converter = tile.asInstanceOf[TileEntityFluidConverter]
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, z)

        // Make sure our lighting is correct, otherwise everything will be black -_-
        val b = tile.getWorldObj.getBlock(x.toInt, y.toInt, z.toInt)
        Tessellator.instance.setBrightness(b.getMixedBrightnessForBlock(tile.getWorldObj, x.toInt, y.toInt, z.toInt))

        // Loop over all block sides to render
        for(side <- ForgeDirection.VALID_DIRECTIONS) {
            val element = converter.getFluidElement(side)

            var icon = Blocks.sponge.getBlockTextureFromSide(0)//TODO: TEMP
            if(element != null) {
                icon = element.getFluid().getIcon
            }

            val t = Tessellator.instance
            t.setColorRGBA(255, 255, 255, 255)
            t.startDrawingQuads()

            val c = coordinates(side.ordinal())
            t.addVertexWithUV(c(0)(0), c(0)(1), c(0)(2), icon.getMinU, icon.getMinV)
            t.addVertexWithUV(c(1)(0), c(1)(1), c(1)(2), icon.getMinU, icon.getMaxV)
            t.addVertexWithUV(c(2)(0), c(2)(1), c(2)(2), icon.getMaxU, icon.getMaxV)
            t.addVertexWithUV(c(3)(0), c(3)(1), c(3)(2), icon.getMaxU, icon.getMinV)

            t.draw()
        }
        GL11.glPopMatrix()
    }

}
