package org.cyclops.fluidconverters

import java.io.File

import org.cyclops.fluidconverters.block.{ItemBlockFluidConverter, BlockFluidConverter}
import cpw.mods.fml.common.{SidedProxy, Mod}
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.registry.GameRegistry
import org.cyclops.fluidconverters.config.{FluidGroupRegistry, ConfigLoader}
import org.cyclops.fluidconverters.tileentity.TileEntityFluidConverter
import cpw.mods.fml.common.Mod.EventHandler

/**
 * The main mod class of FluidConverters.
 * @author rubensworks
 *
 */
@Mod(modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    useMetadata = true,
    version = Reference.MOD_VERSION,
    modLanguage = Reference.SCALA
    )
object FluidConverters {

    @SidedProxy(clientSide = "org.cyclops.fluidconverters.ClientProxy", serverSide = "org.cyclops.fluidconverters.CommonProxy")
    var proxy : CommonProxy = null
    var rootFolder : File = null

    @EventHandler
    def preInit(event: FMLPreInitializationEvent) {
        val rootFolderName = "%s/%s".format(event.getModConfigurationDirectory, Reference.MOD_ID)
        rootFolder = ConfigLoader.init(rootFolderName)
    }
    
    @EventHandler
    def init(event: FMLInitializationEvent) {
        registerFluidConverterBlock()
        proxy.registerRenderers()
    }
    
    @EventHandler
    def postInit(event: FMLPostInitializationEvent) {
        LoggerHelper.log("Loading fluid converters configs...")
        ConfigLoader.findFluidGroups(rootFolder).foreach(fluidGroup => FluidGroupRegistry.registerGroup(fluidGroup))
        FluidColorAnalyzer.init()
    }
    
    private def registerFluidConverterBlock() {
        GameRegistry.registerBlock(BlockFluidConverter, classOf[ItemBlockFluidConverter], BlockFluidConverter.NAMEDID)
        BlockFluidConverter.setCreativeTab(FluidConvertersTab)
        GameRegistry.registerTileEntity(classOf[TileEntityFluidConverter], BlockFluidConverter.NAMEDID)
    }

}