package org.cyclops.fluidconverters.modcompat.waila

import java.util

import mcp.mobius.waila.api._
import net.minecraft.item.ItemStack
import net.minecraft.util.{StatCollector, EnumChatFormatting}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import org.cyclops.fluidconverters.tileentity.TileEntityFluidConverter

/**
 * @author rubensworks
 */
object Waila {

    /**
     * Waila callback.
     * @param registrar The Waila registrar.
     */
    def callbackRegister(registrar: IWailaRegistrar) {
        registrar.registerBodyProvider(DataProvider, classOf[TileEntityFluidConverter])
        registrar.registerTailProvider(DataProvider, classOf[TileEntityFluidConverter])
    }

    object DataProvider extends IWailaDataProvider {

        override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = null

        override def getWailaHead(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor,
                                  config: IWailaConfigHandler): util.List[String] = currenttip

        private def runForTile(accessor: IWailaDataAccessor, currenttip: util.List[String],
                               callback: (IWailaDataAccessor, util.List[String], TileEntityFluidConverter) => Unit):
        util.List[String] = {
            accessor.getTileEntity match {
                case tile: TileEntityFluidConverter =>
                    val tile = accessor.getTileEntity.asInstanceOf[TileEntityFluidConverter]
                    callback(accessor, currenttip, tile)
            }
            currenttip
        }

        override def getWailaBody(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor,
                                  config: IWailaConfigHandler): util.List[String] = {
            runForTile(accessor, currenttip, (accessor: IWailaDataAccessor, currenttip: util.List[String],
                                               tile: TileEntityFluidConverter) => {
                val group = tile.getFluidGroup
                if (group != null) {
                    currenttip.add("%s%s: %s".format(EnumChatFormatting.GOLD, StatCollector.translateToLocal(
                        "tile.blocks.fluidConverter.converter"), group.getGroupName))
                } else {
                    currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("info.invalid"))
                }
            })
        }

        override def getWailaTail(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor,
                                  config: IWailaConfigHandler): util.List[String] = {
            runForTile(accessor, currenttip, (accessor: IWailaDataAccessor, currenttip: util.List[String],
                                              tile: TileEntityFluidConverter) => {
                val group = tile.getFluidGroup
                if (group != null) {
                    ForgeDirection.VALID_DIRECTIONS.foreach((side) => {
                        val element = tile.getFluidElement(side)
                        if (element != null) {
                            currenttip.add("%s%s: %s".format(EnumChatFormatting.DARK_GRAY, side.toString,
                                element.getFluid.getLocalizedName(new FluidStack(element.getFluid, 1))))
                        }
                    })
                }
            })
        }

    }

}
