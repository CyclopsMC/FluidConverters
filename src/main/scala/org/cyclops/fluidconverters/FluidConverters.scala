package org.cyclops.fluidconverters

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.Mod.EventHandler

/**
 * The main mod class of NotEnoughLoot.
 * @author rubensworks
 *
 */
@Mod(modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    useMetadata = true,
    version = Reference.MOD_VERSION,
    modLanguage = Reference.SCALA
    )
object NotEnoughLoot {
  
    @EventHandler
    def preInit(event: FMLPreInitializationEvent) {
    	println("HI!");
    }
    
    @EventHandler
    def init(event: FMLInitializationEvent) {
    
    }
    
    @EventHandler
    def postInit(event: FMLPostInitializationEvent) {
    	
    }

}