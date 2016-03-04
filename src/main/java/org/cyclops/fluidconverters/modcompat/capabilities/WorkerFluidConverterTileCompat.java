package org.cyclops.fluidconverters.modcompat.capabilities;

import org.cyclops.commoncapabilities.api.capability.work.IWorker;
import org.cyclops.cyclopscore.modcompat.ICapabilityCompat;
import org.cyclops.fluidconverters.Capabilities;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

/**
 * Compatibility for fluid converter worker capability.
 * @author rubensworks
 */
public class WorkerFluidConverterTileCompat implements ICapabilityCompat<TileFluidConverter> {

    @Override
    public void attach(final TileFluidConverter provider) {
        provider.addCapabilityInternal(Capabilities.WORKER, new Worker(provider));
    }

    public static class Worker implements IWorker {

        private final TileFluidConverter provider;

        public Worker(TileFluidConverter provider) {
            this.provider = provider;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean hasWork() {
            return provider.fillSides(true);
        }

        @Override
        public boolean canWork() {
            return provider.isValidConverter();
        }
    }
}
