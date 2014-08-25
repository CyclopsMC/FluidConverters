package org.cyclops.fluidconverters.config

import net.minecraftforge.fluids.FluidRegistry
import org.apache.logging.log4j.Level
import org.cyclops.fluidconverters.LoggerHelper

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
        for(element <- group.getFluidElements) {
            if(!FluidRegistry.isFluidRegistered(element.getFluidName)) {
                LoggerHelper.log(Level.WARN, "The fluid %s in group %s is not registered because it does not exist."
                        .format(element.getFluidName, group.getGroupId))
            }
        }
        groups.put(group.getGroupId, group) != None
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

}
