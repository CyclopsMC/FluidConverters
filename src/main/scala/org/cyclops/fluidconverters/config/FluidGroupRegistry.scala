package org.cyclops.fluidconverters.config

import net.minecraftforge.fluids.FluidRegistry
import org.apache.logging.log4j.Level
import org.cyclops.fluidconverters.LoggerHelper

import scala.collection.mutable.Set

/**
 * Registry of all the fluid groups that are convertable.
 * @author rubensworks
 */
object FluidGroupRegistry {

    val groups = Set[FluidGroup]()

    /**
     * Register a new fluid group.
     * @param group The group to register.
     */
    def registerGroup(group: FluidGroup) : Boolean = {
        val canAdd = group.getFluidElements.foldLeft(true)(
            (prev, element) => prev && FluidRegistry.isFluidRegistered(element.getFluidName) && !isFluidRegistered(element.getFluidName)
        )
        if(canAdd) {
            groups += group
        } else {
            LoggerHelper.log(Level.WARN,
                "Skipped registration of a group because a fluid is already registered somewhere else.")
        }
        canAdd
    }

    /**
     * Get all the registered groups.
     * @return The set of groups.
     */
    def getGroups : Set[FluidGroup] = {
        groups
    }

    /**
     * Check if a fluid is already registered in some group.
     * @param fluidName The name of the fluid to look for.
     * @return If the fluid is already registered.
     */
    def isFluidRegistered(fluidName: String): Boolean = {
        groups.foldLeft(false)(
            (prev, group) => prev || group.getFluidElements.foldLeft(false)(
                (prev2, fluidElement) => prev2 || fluidElement.getFluidName.equals(fluidName)
            ))
    }

}
