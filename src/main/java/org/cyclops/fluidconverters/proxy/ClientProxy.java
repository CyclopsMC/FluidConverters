package org.cyclops.fluidconverters.proxy;

import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.fluidconverters.FluidConverters;

/**
 * Proxy for the client side.
 * 
 * @author rubensworks
 * 
 */
public class ClientProxy extends ClientProxyComponent {

	public ClientProxy() {
		super(new CommonProxy());
	}

	@Override
	public ModBase getMod() {
		return FluidConverters._instance;
	}
    
}
