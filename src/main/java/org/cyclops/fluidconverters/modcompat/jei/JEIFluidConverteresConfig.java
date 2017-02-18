package org.cyclops.fluidconverters.modcompat.jei;

import mezz.jei.api.*;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup.FluidElement;
import org.cyclops.fluidconverters.modcompat.jei.fluidconverter.FluidConverterRecipeCategory;
import org.cyclops.fluidconverters.modcompat.jei.fluidconverter.FluidConverterRecipeHandler;
import org.cyclops.fluidconverters.modcompat.jei.fluidconverter.FluidConverterRecipeJEI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Helper for registering JEI manager.
 * @author runesmacher
 */
@JEIPlugin
public class JEIFluidConverteresConfig extends BlankModPlugin implements IModPlugin {

    public static IJeiHelpers JEI_HELPER;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(BlockFluidConverter.createItemStack(null).getItem());
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        if (JEIModCompat.canBeUsed) {
            JEI_HELPER = registry.getJeiHelpers();
            // Fluid converter
            this.register(registry, JEI_HELPER.getGuiHelper());
        }
    }

    public void register(IModRegistry registry, IGuiHelper guiHelper) {
        registry.addRecipeHandlers(new FluidConverterRecipeHandler());
        List<FluidConverterRecipeJEI> result = new ArrayList<FluidConverterRecipeJEI>();

        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup group = it.next();
            registry.addRecipeCategories(new FluidConverterRecipeCategory(registry, guiHelper, group));

            List<FluidElement> fluidElementList = group.getFluidElements();
            for (FluidElement inputFluid : fluidElementList) {
                for (FluidElement outputFluid : fluidElementList) {
                    if (inputFluid != outputFluid) {
                        result.add(new FluidConverterRecipeJEI(inputFluid, outputFluid, group));
                    }
                }
            }
        }
        registry.addRecipes(result);
    }
}
