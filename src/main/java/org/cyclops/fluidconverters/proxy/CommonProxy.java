package org.cyclops.fluidconverters.proxy;

import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.proxy.CommonProxyComponent;
import org.cyclops.fluidconverters.FluidConverters;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return FluidConverters._instance;
    }

}
