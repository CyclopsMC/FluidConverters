package org.cyclops.fluidconverters;

import org.cyclops.cyclopscore.config.configurable.ConfigurableBlock;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBlockContainer;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;

/**
 * Class that can hold basic static things that are better not hard-coded
 * like mod details, texture paths, ID's...
 * @author immortaleeb
 *
 */
@SuppressWarnings("javadoc")
public class Reference {
	
    // Mod info
    public static final String MOD_ID = "fluidconverters";
    public static final String MOD_NAME = "FluidConverters";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_BUILD_NUMBER = "@BUILD_NUMBER@";
    public static final String MOD_CHANNEL = MOD_ID;
    public static final String MOD_MC_VERSION = "@MC_VERSION@";
    public static final String GA_TRACKING_ID = "UA-65307010-6";
    public static final String VERSION_URL = "https://raw.githubusercontent.com/CyclopsMC/Versions/master/1.9/FluidConverters.txt";
    
    // Paths
    public static final String TEXTURE_PATH_GUI = "textures/gui/";
    public static final String TEXTURE_PATH_SKINS = "textures/skins/";
    public static final String TEXTURE_PATH_MODELS = "textures/models/";
    public static final String TEXTURE_PATH_ENTITIES = "textures/entities/";
    public static final String TEXTURE_PATH_GUIBACKGROUNDS = "textures/gui/title/background/";
    public static final String TEXTURE_PATH_ITEMS = "textures/items/";
    public static final String TEXTURE_PATH_PARTICLES = "textures/particles/";
    public static final String MODEL_PATH = "models/";

    public static final String ASSETS_PATH = "/assets/" + MOD_ID + "/";
    
    // MOD ID's
    public static final String MOD_FORGE = "Forge";
    public static final String MOD_FORGE_VERSION = "@FORGE_VERSION@";
    public static final String MOD_FORGE_VERSION_MIN = "12.17.0.1909";
    public static final String MOD_CYCLOPSCORE = "cyclopscore";
    public static final String MOD_CYCLOPSCORE_VERSION = "@CYCLOPSCORE_VERSION@";
    public static final String MOD_CYCLOPSCORE_VERSION_MIN = "0.7.0";
    public static final String MOD_WAILA = "Waila";
    
    // Dependencies
    public static final String MOD_DEPENDENCIES =
            "required-after:" + MOD_FORGE       + "@[" + MOD_FORGE_VERSION_MIN       + ",);" +
            "required-after:" + MOD_CYCLOPSCORE + "@[" + MOD_CYCLOPSCORE_VERSION_MIN + ",);";

    /**
     * Adds "modid:" as a prefix to the given string.
     * @return The given string prefixed with "modid:"
     */
    public static final String prefixModId(String s) {
        return MOD_ID + ":" + s;
    }

    /**
     * Prepends "modid:" to the named id of the given config.
     * @param extendedConfig config which provides a namedid
     * @return named id prepended with "modid:"
     */
    public static final String prefixModId(ExtendedConfig<?> extendedConfig) {
        return prefixModId(extendedConfig.getNamedId());
    }

    /**
     * Prepends "modid:" to the named id of the given block
     * @param block configurable block which provides a namedid
     * @return named id of the block prepended with "modid:"
     */
    public static final String prefixModId(ConfigurableBlock block) {
        return prefixModId(block.getConfig());
    }

    /**
     * Prepends "modid:" to the named id of the given block container
     * @param block configurable block container which provides a namedid
     * @return named id of the block container prepended with "modid:"
     */
    public static final String prefixModId(ConfigurableBlockContainer block) {
        return prefixModId(block.getConfig());
    }
}
