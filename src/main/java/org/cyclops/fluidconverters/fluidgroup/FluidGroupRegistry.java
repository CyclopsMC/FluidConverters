package org.cyclops.fluidconverters.fluidgroup;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds all loaded fluid groups.
 */
public class FluidGroupRegistry {
    private static final List<FluidGroup> fluidGroups = new LinkedList<FluidGroup>();;

    /**
     * Registers a new fluid group to the registry.
     * @param fluidGroup fluid group to be registered.
     */
    public static void registerFluidGroup(FluidGroup fluidGroup) {
        fluidGroups.add(fluidGroup);
    }

    /**
     * Registers a whole list of fluid groups to the registry.
     * @param fluidGroups list of fluid groups to be registered.
     */
    public static void registerFluidGroupList(List<FluidGroup> fluidGroups) {
        for (FluidGroup fluidGroup : fluidGroups) {
            registerFluidGroup(fluidGroup);
        }
    }

    /**
     * @return Returns an iterator over all registered fluid groups.
     */
    public static Iterator<FluidGroup> iterator() {
        return fluidGroups.iterator();
    }
}
