package org.cyclops.fluidconverters.config

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidStack, FluidContainerRegistry, FluidRegistry}
import net.minecraftforge.oredict.ShapedOreRecipe
import org.apache.logging.log4j.Level
import org.cyclops.fluidconverters.LoggerHelper
import org.cyclops.fluidconverters.block.BlockFluidConverter

import scala.collection.mutable.Map

/**
 * Registry of all the fluid groups that are convertable.
 * @author rubensworks
 */
object FluidGroupRegistry {

    val groups = Map[String, FluidGroup]()

    /**
     * Register a new fluid group.
     * @param group The group to register.
     * @return If the adding succeeded.
     */
    def registerGroup(group: FluidGroup) : Boolean = {
        var foundOne = false
        for(element <- group.getFluidElements) {
            if(!FluidRegistry.isFluidRegistered(element.getFluidName)) {
                LoggerHelper.log(Level.WARN, "The fluid %s in group %s is not registered because it does not exist."
                        .format(element.getFluidName, group.getGroupId))
            } else {
                foundOne = true
            }
        }
        if(foundOne) {
            groups.put(group.getGroupId, group)
            return true
        }
        false
    }

    /**
     * Get a fluid group by id.
     * @param groupId The id of the fluid group.
     * @return The group with the given id, can be null.
     */
    def getGroup(groupId : String) : FluidGroup = {
        groups.get(groupId) match {
            case Some(i) => i
            case None => null
        }
    }

    /**
     * Get all the registered groups.
     * @return The set of groups.
     */
    def getGroups : Iterable[FluidGroup] = {
        groups.values
    }

    /**
     * Check if a fluid is already registered in some group.
     * @param fluidName The name of the fluid to look for.
     * @return If the fluid is already registered.
     */
    def isFluidRegistered(fluidName: String): Boolean = {
        groups.values.foldLeft(false)(
            (prev, group) => prev || group.getFluidElements.foldLeft(false)(
                (prev2, fluidElement) => prev2 || fluidElement.getFluidName.equals(fluidName)
            ))
    }

    def registerRecipes() {
        for(group <- groups.values) {
            if(group.hasRecipe) {
                val result = new ItemStack(BlockFluidConverter)
                BlockFluidConverter.addGroupInfo(result, group)
                for (element <- group.getFluidElements) {
                    if(element.getFluid != null) {
                        val container: ItemStack = FluidContainerRegistry.fillFluidContainer(new FluidStack(element.getFluid, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.bucket))
                        if (container != null) {
                            group.registerRecipe(result, container)
                        }
                    }
                }
            }
        }
    }

}
