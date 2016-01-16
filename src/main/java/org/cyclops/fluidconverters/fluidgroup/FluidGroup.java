package org.cyclops.fluidconverters.fluidgroup;

import lombok.Data;
import lombok.NonNull;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

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
    }

    public static class NoSuchFluidException extends Exception {
        public NoSuchFluidException(String message) {
            super(message);
        }
    }
}
