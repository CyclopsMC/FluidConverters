package org.cyclops.fluidconverters.modcompat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup.FluidElement;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.cyclops.fluidconverters.modcompat.jei.fluidconverter.FluidConverterRecipeCategory;
import org.cyclops.fluidconverters.modcompat.jei.fluidconverter.FluidConverterRecipeJEI;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * Helper for registering JEI manager.
 * @author runesmacher
 */
@JEIPlugin
public class JEIFluidConvertersConfig implements IModPlugin {

    public static IJeiHelpers JEI_HELPER;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(BlockFluidConverter.createItemStack(null).getItem());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup group = it.next();
            registry.addRecipeCategories(new FluidConverterRecipeCategory(registry.getJeiHelpers().getGuiHelper(), group));
        }
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        if (JEIModCompat.canBeUsed) {
            JEI_HELPER = registry.getJeiHelpers();
            this.registerFluidConverters(registry);
        }
    }

    public void registerFluidConverters(IModRegistry registry) {
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            List<FluidConverterRecipeJEI> result = Lists.newArrayList();
            FluidGroup group = it.next();
            String category = FluidConverterRecipeCategory.getCategoryUid(group);
            registry.addRecipeCatalyst(BlockFluidConverter.createItemStack(group), category);

            List<FluidElement> fluidElementList = group.getFluidElements();
            for (FluidElement inputFluid : fluidElementList) {
                for (FluidElement outputFluid : fluidElementList) {
                    if (inputFluid != outputFluid) {
                        result.add(new FluidConverterRecipeJEI(inputFluid, outputFluid, group));
                    }
                }
            }
            registry.addRecipes(result, category);
        }
    }
}
