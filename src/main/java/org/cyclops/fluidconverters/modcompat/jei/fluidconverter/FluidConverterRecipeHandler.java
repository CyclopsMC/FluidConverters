package org.cyclops.fluidconverters.modcompat.jei.fluidconverter;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;

/**
 * Handler for the Fluid Converter recipes.
 * @author runesmacher
 */
public class FluidConverterRecipeHandler implements IRecipeHandler<FluidConverterRecipeJEI> {
    private String uid;
    public FluidConverterRecipeHandler(@Nonnull String uid) {
      this.uid = uid;
    }

    @Override
    public Class<FluidConverterRecipeJEI> getRecipeClass() {
      return FluidConverterRecipeJEI.class;
    }

    @Override
    public String getRecipeCategoryUid() {
      return uid;
    }

    @Override
    public FluidConverterRecipeJEI getRecipeWrapper(FluidConverterRecipeJEI recipe) {
      return recipe;
    }

    @Override
    public boolean isRecipeValid(FluidConverterRecipeJEI recipe) {
      return true;
    }

    @Override
    public String getRecipeCategoryUid(FluidConverterRecipeJEI recipe) {
      return uid;
    }
}
