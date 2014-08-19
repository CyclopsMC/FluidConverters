package org.cyclops.fluidconverters.config;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * A simple fluid element holder that also contains a numerical cost.
 * @author rubensworks
 */
public class FluidElement {

    private String fluidName;
    private double value;

    /**
     * @return The name of the fluid.
     */
    public String getFluidName() {
        return fluidName;
    }

    /**
     * @return The value of the fluid.
     */
    public double getValue() {
        return value;
    }

    /**
     * @return The fluid
     */
    public Fluid getFluid() {
        return FluidRegistry.getFluid(getFluidName().toLowerCase());
    }

}
