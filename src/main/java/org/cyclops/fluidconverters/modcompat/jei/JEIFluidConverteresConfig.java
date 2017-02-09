package org.cyclops.fluidconverters.modcompat.jei;

import mezz.jei.api.*;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.modcompat.jei.fluidconverter.FluidConverterRecipeCategory;

import javax.annotation.Nonnull;

/**
 * Helper for registering JEI manager.
 * @author runesmacher
 *
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
        if(JEIModCompat.canBeUsed) {
            JEI_HELPER = registry.getJeiHelpers();
            // Fluid converter
              FluidConverterRecipeCategory.register(registry,JEI_HELPER.getGuiHelper());
        }
    }
}
