package org.cyclops.fluidconverters.block;

import net.minecraft.item.ItemBlock;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.ConfigurableTypeCategory;
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
     * The amount of mB per tick that can be converted.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.MACHINE, comment = "The amount of mB per tick that can be converted.", isCommandable = true)
    public static int mBRate = 100;

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

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockFluidConverter.class;
    }
}
