package org.cyclops.fluidconverters.block;

import net.minecraft.block.material.Material;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBlockContainer;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

/**
 * A block that converts fluids, registered as fluid groups in the FluidGroupRegistry.
 * @author immortaleeb
 */
public class BlockFluidConverter extends ConfigurableBlockContainer {

    private static BlockFluidConverter _instance = null;

    /**
     * Get the unique instance of this class.
     *
     * @return The unique instance.
     */
    public static BlockFluidConverter getInstance() {
        return _instance;
    }

    /**
     * Make a new fluid converter block instance.
     *
     * @param eConfig    Config for this blockState.
     */
    public BlockFluidConverter(ExtendedConfig eConfig) {
        super(eConfig, Material.iron, TileFluidConverter.class);
    }
}
