package org.cyclops.fluidconverters.modcompat.jei.fluidconverter;

import org.cyclops.fluidconverters.Reference;
import mezz.jei.api.recipe.IRecipeHandler;

/**
 * Handler for the Fluid Converter recipes.
 * @author runesmacher
 */
public class FluidConverterRecipeHandler implements IRecipeHandler<FluidConverterRecipeJEI> {
    @Override
    public Class<FluidConverterRecipeJEI> getRecipeClass() {
        return FluidConverterRecipeJEI.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return Reference.MOD_ID + ":" + "null";
    }

    @Override
    public String getRecipeCategoryUid(FluidConverterRecipeJEI recipe) {
        return Reference.MOD_ID + ":" + recipe.getFluidGroup().getGroupId();
    }

    @Override
    public FluidConverterRecipeJEI getRecipeWrapper(FluidConverterRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(FluidConverterRecipeJEI recipe) {
        return recipe.getFluidGroup() != null;
    }
}
