package org.cyclops.fluidconverters.modcompat.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.cyclopscore.modcompat.capabilities.SimpleCapabilityConstructor;
import org.cyclops.fluidconverters.Capabilities;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

import javax.annotation.Nullable;

/**
 * Compatibility for fluid converter worker capability.
 * @author rubensworks
 */
public class WorkerFluidConverterTileCompat extends SimpleCapabilityConstructor<IWorker, TileFluidConverter> {

    @Nullable
    @Override
    public ICapabilityProvider createProvider(TileFluidConverter host) {
        return new DefaultCapabilityProvider<IWorker>(Capabilities.WORKER, new Worker(host));
    }

    @Override
    public Capability<IWorker> getCapability() {
        return Capabilities.WORKER;
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
