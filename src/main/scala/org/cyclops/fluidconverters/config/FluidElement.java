package org.cyclops.fluidconverters.config;

/**
 * A simple fluid element holder that also contains a numerical cost.
 * @author rubensworks
 */
public class FluidElement {

    private String fluidName;
    private double cost;

    /**
     * @return The name of the fluid.
     */
    public String getFluidName() {
        return fluidName;
    }

    /**
     * @return The cost of the fluid.
     */
    public double getCost() {
        return cost;
    }

}
