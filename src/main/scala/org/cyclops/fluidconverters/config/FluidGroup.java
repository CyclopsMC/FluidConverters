package org.cyclops.fluidconverters.config;

import net.minecraftforge.fluids.Fluid;

/**
 * Holder class for fluid groups
 * @author rubensworks
 */
public class FluidGroup {

    private String groupId;
    private String groupName = null;
    private FluidElement[] fluidElements;
    private double lossRatio = 0.0;

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        if(groupName == null) {
            return groupId;
        }
        return groupName;
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

    public double getLossRatio() {
        return lossRatio;
    }

}
