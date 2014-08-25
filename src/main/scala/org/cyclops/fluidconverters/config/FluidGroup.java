package org.cyclops.fluidconverters.config;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.ShapedOreRecipe;

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

    public void registerRecipe(ItemStack result, ItemStack container) {
        GameRegistry.addRecipe(new ShapedOreRecipe(result, true,
                new Object[]{
                        "I I",
                        "GBG",
                        "I I",
                        'B', container,
                        'G', new ItemStack(Items.gold_nugget),
                        'I', new ItemStack(Items.iron_ingot)
                }
        ));
    }

}
