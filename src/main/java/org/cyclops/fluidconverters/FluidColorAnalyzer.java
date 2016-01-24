package org.cyclops.fluidconverters;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.RenderHelpers;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.lwjgl.util.Color;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Analyzes the colors of fluids in fluid groups and calculates average colors.
 * @author immortaleeb
 */
public class FluidColorAnalyzer {

    private static final Map<FluidGroup, Color> fluidGroupColors = new HashMap<FluidGroup, Color>();

    /**
     * Forces the color analyzer to calculate all average colors for all fluid
     * groups and cache them for later use.
     */
    public static void calculateAverageColors() {
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup group = it.next();
            fluidGroupColors.put(group, calculateAverageColor(group));
        }
    }

    /**
     * Gets the cached average color of the given fluid group
     * @param fluidGroup the fluid group
     * @return The average color of all fluids in the fluid group
     */
    public static Color getAverageColor(FluidGroup fluidGroup) {
        return fluidGroupColors.get(fluidGroup);
    }

    /**
     * Calculates the average color of a single fluid
     * @param fluid The fluid for which we need to calculate the average color
     * @return Average color of the fluid, or white in case it could not be calculated
     */
    private static Color calculateAverageColor(Fluid fluid) {
        TextureAtlasSprite icon = RenderHelpers.getFluidIcon(fluid, EnumFacing.UP);
        int[] texture = icon.getFrameTextureData(0)[0];
        float r = 0, g = 0, b = 0;
        for (int pixel : texture) {
            Triple<Float, Float, Float> triple = Helpers.intToRGB(pixel);
            r += triple.getLeft();
            g += triple.getMiddle();
            b += triple.getRight();
        }

        r /= texture.length;
        g /= texture.length;
        b /= texture.length;

        return new Color((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    /**
     * Calculates the average color of all fluids in the fluidgroup
     * @param fluidGroup the fluid group
     * @return The average color of all fluids in the fluid group, or white in case it could not be calculated
     */
    private static Color calculateAverageColor(FluidGroup fluidGroup) {
        int r = 0;
        int g = 0;
        int b = 0;

        List<FluidGroup.FluidElement> fluidElements = fluidGroup.getFluidElements();
        int size = fluidElements.size();
        for (FluidGroup.FluidElement fluidElement : fluidElements) {
            Color color = calculateAverageColor(fluidElement.getFluid());
            r += color.getRed();
            g += color.getGreen();
            b += color.getBlue();
        }

        r = r / size;
        g = g / size;
        b = b / size;

        return new Color(r, g, b);
    }
}
