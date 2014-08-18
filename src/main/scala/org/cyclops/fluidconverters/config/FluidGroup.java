package org.cyclops.fluidconverters.config;

import net.minecraftforge.fluids.Fluid;

/**
 * Holder class for fluid groups
 * @author rubensworks
 */
public class FluidGroup {

    private String groupId;
    private FluidElement[] fluidElements;

    public String getGroupId() {
        return groupId;
    }

    public FluidElement[] getFluidElements() {
        return fluidElements;
    }

    public FluidElement getFluidElement(Fluid fluid) {
        return getFluidElement(fluid.getName());
    }

    public FluidElement getFluidElement(String fluidName) {
        for(FluidElement fluidElement : getFluidElements()) {
            if(fluidElement.getFluid().getName().equals(fluidName)) {
                return fluidElement;
            }
        }
        return null;
    }

}
