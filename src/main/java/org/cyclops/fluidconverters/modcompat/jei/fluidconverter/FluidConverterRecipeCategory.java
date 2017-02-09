package org.cyclops.fluidconverters.modcompat.jei.fluidconverter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.fluidconverters.FluidConverters;
import org.cyclops.fluidconverters.Reference;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.block.BlockFluidConverterConfig;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup.FluidElement;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Category for the Fluid converter recipes.
 * @author runesmacher
 */
public class FluidConverterRecipeCategory extends BlankRecipeCategory<FluidConverterRecipeJEI>  {

    private static final String CATEGORY = Reference.MOD_ID + ":fluidConverter";
    private static final int FLUIDINPUT_SLOT = 0;
    private static final int FLUIDOUTPUT_SLOT = 1;

    public static void register(IModRegistry registry, IGuiHelper guiHelper) {

     registry.addRecipeCategories(new FluidConverterRecipeCategory(guiHelper));
     registry.addRecipeHandlers(new FluidConverterRecipeHandler<FluidConverterRecipeJEI>(FluidConverterRecipeJEI.class, CATEGORY));

     long start = System.nanoTime();

     List<FluidConverterRecipeJEI> result = new ArrayList<FluidConverterRecipeJEI>();

     Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
      while (it.hasNext()) {
          FluidGroup group = it.next();
          registry.addRecipeCategoryCraftingItem(BlockFluidConverter.createItemStack(group), CATEGORY);

          List<FluidElement> fluidElementList = group.getFluidElements();
          for (FluidElement inputFluid : fluidElementList) {
              for (FluidElement outputFluid : fluidElementList) {
                  if(inputFluid != outputFluid){
                      result.add(new FluidConverterRecipeJEI(inputFluid, outputFluid, group));
                  }
              }
          }
      }

      long end = System.nanoTime();
      registry.addRecipes(result);

      System.out.println(String.format("FluidConverterRecipeCategory: Added %d flluid converter recipes to JEI in %.3f seconds.", result.size(),
          (end - start) / 1000000000d));
    }

    // ------------ Category

    @Nonnull
    private final IDrawable background;
    @Nonnull
    protected final IDrawableAnimated arrow;

    public FluidConverterRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation resourceLocation = new ResourceLocation(Reference.MOD_ID + ":"
                + FluidConverters._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                + BlockFluidConverterConfig._instance.getNamedId() + "_gui_jei.png");
        this.background = guiHelper.createDrawable(resourceLocation, 0, 0, 93, 53);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(resourceLocation, 94, 0, 24, 16);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }


    @Override
    public @Nonnull String getUid() {
      return  CATEGORY;
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getTitle() {
      return BlockFluidConverter.getInstance().getLocalizedName();
    }

    @Override
    public @Nonnull IDrawable getBackground() {
      return background;
    }

    @Override
    public void drawAnimations(@Nonnull Minecraft minecraft) {
      arrow.draw(minecraft, 35, 20);
    }

    @SuppressWarnings("null")
    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull FluidConverterRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
      IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

      fluidStacks.init(FLUIDINPUT_SLOT, false, 12, 8, 16, 40, 1000, false, null);
        if(recipeWrapper.inputStack != null) {
            fluidStacks.set(FLUIDINPUT_SLOT, recipeWrapper.inputStack);
        }
        fluidStacks.init(FLUIDOUTPUT_SLOT, false, 66, 8, 16, 40, 1000, false, null);
        if(recipeWrapper.outputStack != null) {
            fluidStacks.set(FLUIDOUTPUT_SLOT,recipeWrapper.outputStack);
        }
    }
}
