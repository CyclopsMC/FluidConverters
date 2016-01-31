package org.cyclops.fluidconverters.fluidgroup;

import lombok.Data;
import lombok.NonNull;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.cyclops.fluidconverters.FluidColorAnalyzer;
import org.lwjgl.util.Color;

import java.util.List;

/**
 * A FluidGroup contains information about liquids that can be converted into
 * each other and which ratios apply to the conversion process.
 */
@Data
public class FluidGroup {
    @NonNull
    private String groupId;
    @NonNull
    private List<FluidElement> fluidElements;

    private String groupName;
    private float lossRatio;
    private boolean hasRecipe;

    /**
     * @return Returns the average color of the fluids in this fluid group
     */
    public Color getAverageColor() {
        return FluidColorAnalyzer.getAverageColor(this);
    }

    /**
     * Finds the fluid element with the given fluid.
     * @param fluid Fluid that needs to be matched.
     * @returns Returns the fluid element that matches the given fluid, or null in case no fluid element matched.
     */
    public FluidElement getFluidElementByFluid(Fluid fluid) {
        for (FluidElement fluidElement : fluidElements) {
            if (fluidElement.fluid.equals(fluid))
                return fluidElement;
        }
        return null;
    }

    @Data
    public static class FluidElement {
        private Fluid fluid;
        private float value;

        public FluidElement(String fluidName, float value) throws NoSuchFluidException {
            // Find the fluid with the given name
            this.fluid = FluidRegistry.getFluid(fluidName);
            if (this.fluid == null)
                throw new NoSuchFluidException("No fluid with the name '" + fluidName + "' could be found");

            this.value = value;
        }

        /**
         * Converts a given amount of fluid from this fluid's units to normalized units.
         * @param amount Fluid amount in fluid units.
         * @return Fluid amount in normalized units.
         */
        public float normalize(float amount) {
            return amount * this.value;
        }

        /**
         * Converts a given amount of fluid from normalized units to this fluid's units.
         * @param amount Fluid amount in normalized units.
         * @return Fluid amount in fluid units.
         */
        public float denormalize(float amount) {
            return amount / this.value;
        }
    }

    public static class NoSuchFluidException extends Exception {
        public NoSuchFluidException(String message) {
            super(message);
        }
    }
}
