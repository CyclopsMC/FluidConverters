package org.cyclops.fluidconverters.event;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.fluidconverters.FluidColorAnalyzer;

/**
 * Hook for texture stitch events.
 * @author immortaleeb
 */
@SideOnly(Side.CLIENT)
public class TextureStitchEventHook {
    /**
     * Called once texture stitching is done.
     * This will force us to calculate the average color values of fluid groups
     * as soon as fluid textures become available.
     * @param event Texture stitch post event
     */
    @SubscribeEvent
    public void onPostTextureStitch(TextureStitchEvent.Post event) {
        FluidColorAnalyzer.calculateAverageColors();
    }
}
