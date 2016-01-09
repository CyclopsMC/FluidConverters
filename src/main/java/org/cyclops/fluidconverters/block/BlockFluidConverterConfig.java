package org.cyclops.fluidconverters.block;

import org.cyclops.cyclopscore.config.extendedconfig.BlockContainerConfig;
import org.cyclops.fluidconverters.FluidConverters;

/**
 * Config for {@link BlockFluidConverter}
 */
public class BlockFluidConverterConfig extends BlockContainerConfig {
    /**
     * The unique instance.
     */
    public static BlockFluidConverterConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockFluidConverterConfig() {
        super(
            FluidConverters._instance,
            true,
            "fluidconverter",
            null,
            BlockFluidConverter.class
        );
    }
}
