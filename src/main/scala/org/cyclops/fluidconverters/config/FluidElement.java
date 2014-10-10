package org.cyclops.fluidconverters.config;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
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

    /**
     * Get an icon for this fluid element.
     * It will get the icon in a smart way from the fluid, and if not available, from the bound fluid block.
     * @param side The side to get the icon for.
     * @param defaultWater If the water icon should be returned if all the found icons were null.
     * @return The icon.
     */
    public IIcon getIcon(ForgeDirection side, boolean defaultWater) {
        Fluid fluid = getFluid();
        IIcon icon = fluid.getFlowingIcon();
        if(icon == null || (side == ForgeDirection.UP || side == ForgeDirection.DOWN)) {
            icon = fluid.getStillIcon();
        }
        if(icon == null) {
            try {
                icon = fluid.getBlock().getIcon(side.ordinal(), 0);
            } catch (NullPointerException e) {
                // Do nothing
            }
            if(icon == null && defaultWater) {
                icon = Blocks.water.getIcon(side.ordinal(), 0);
            }
        }
        return icon;
    }

}
