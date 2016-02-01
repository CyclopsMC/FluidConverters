package org.cyclops.fluidconverters.block;

import net.minecraft.item.ItemBlock;
import org.cyclops.cyclopscore.config.extendedconfig.BlockContainerConfig;
import org.cyclops.fluidconverters.FluidConverters;
import org.cyclops.fluidconverters.item.ItemBlockFluidConverter;

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
            "fluidConverter",
            null,
            BlockFluidConverter.class
        );
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockFluidConverter.class;
    }
}
