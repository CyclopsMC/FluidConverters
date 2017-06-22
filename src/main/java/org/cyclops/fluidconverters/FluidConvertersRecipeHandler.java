package org.cyclops.fluidconverters;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.init.RecipeHandler;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Handles all recipe for crafting fluid converters.
 * @author immortaleeb
 */
public class FluidConvertersRecipeHandler extends RecipeHandler {
    public FluidConvertersRecipeHandler(ModBase mod, String... fileNames) {
        super(mod, fileNames);
    }

    private String predefinedId(FluidGroup fluidGroup) {
        return Reference.prefixModId(BlockFluidConverter.getInstance()) + "_" + fluidGroup.getGroupId();
    }

    @Override
    protected void loadPredefineds(Map<String, ItemStack> predefinedItems, Set<String> predefinedValues) {
        super.loadPredefineds(predefinedItems, predefinedValues);

        // Add predefined items for every single fluid converter
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup fluidGroup = it.next();
            predefinedItems.put(predefinedId(fluidGroup), BlockFluidConverter.createItemStack(fluidGroup));
        }
    }

    @Override
    protected void registerCustomRecipes() {
        super.registerCustomRecipes();

        // Register a recipe for all fluid groups that have the default recipe enabled
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup fluidGroup = it.next();

            // Only register if default recipes are enabled
            if (fluidGroup.isHasDefaultRecipe()) {
                ItemStack result = getPredefinedItem(predefinedId(fluidGroup));

                // Add a recipe for every fluid in the fluid group
                for (FluidGroup.FluidElement el : fluidGroup.getFluidElements()) {

                    // Create a filled container
                    ItemStack container = new ItemStack(Items.BUCKET);
                    IFluidHandlerItem fluidHandler = container.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

                    if(fluidHandler != null
                            && fluidHandler.fill(new FluidStack(el.getFluid(), Fluid.BUCKET_VOLUME), true) == Fluid.BUCKET_VOLUME) {
                        // Create the default recipe
                        GameRegistry.addRecipe(new ShapedOreRecipe(result, true,
                                new Object[]{
                                        "I I",
                                        "GBG",
                                        "I I",
                                        'B', fluidHandler.getContainer(),
                                        'G', new ItemStack(Items.GOLD_NUGGET),
                                        'I', new ItemStack(Items.IRON_INGOT)
                                }
                        ));
                    } else {
                        getMod().log(Level.WARN, String.format("Skipped registering default fluid converter crafting " +
                                "recipe for fluid groups %s due to a non-existing fluid container for fluid %s",
                                fluidGroup.getGroupName(), el.getFluid().getName()));
                    }
                }
            }
        }
    }
}
