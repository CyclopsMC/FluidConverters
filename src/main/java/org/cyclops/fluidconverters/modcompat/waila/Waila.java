package org.cyclops.fluidconverters.modcompat.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.fluidconverters.Reference;
import org.cyclops.fluidconverters.block.BlockFluidConverter;

/**
 * Waila support class.
 * @author immortaleeb
 */
public class Waila {
    /**
     * Waila callback.
     * @param registrar The Waila registrar.
     */
    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.addConfig(Reference.MOD_NAME, getBlockInfoConfigID(), 
                L10NHelpers.localize("gui." + Reference.MOD_ID + ".waila.blockInfoConfig"));

        // Fluid converter block info
        registrar.registerBodyProvider(new FluidConverterInfoDataProvider(), BlockFluidConverter.class);
    }

    private static String getBlockInfoConfigID() {
        return Reference.MOD_ID + ".blockInfo";
    }
}
