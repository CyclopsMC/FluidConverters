package org.cyclops.fluidconverters.fluidgroup;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds all loaded fluid groups.
 */
public class FluidGroupRegistry {
    private static final Map<String, FluidGroup> fluidGroups = new TreeMap<String, FluidGroup>();;

    /**
     * Finds the fluid group that corresponds to the given fluid group id.
     * @param fluidGroupId Returns the fluid group, or null in case no fluid group was found
     */
    public static FluidGroup getFluidGroupById(String fluidGroupId) {
        return fluidGroups.get(fluidGroupId);
    }

    /**
     * Registers a new fluid group to the registry.
     * @param fluidGroup fluid group to be registered.
     */
    public static void registerFluidGroup(FluidGroup fluidGroup) {
        fluidGroups.put(fluidGroup.getGroupId(), fluidGroup);
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
        return fluidGroups.values().iterator();
    }
}
