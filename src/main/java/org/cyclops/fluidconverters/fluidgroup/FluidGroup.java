package org.cyclops.fluidconverters.fluidgroup;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * A FluidGroup contains information about liquids that can be converted into
 * each other and which ratios apply to the conversion process.
 */
@Data
public class FluidGroup {
    @NonNull
    private String groupId;
    @NonNull
    private List<FluidElement> fluidElements;

    private String groupName;
    private float lossRatio;
    private boolean hasRecipe;

    @Data
    public static class FluidElement {
        @NonNull
        private String fluidName;
        @NonNull
        private float value;
    }
}
