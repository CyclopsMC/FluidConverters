package org.cyclops.fluidconverters.proxy;

import net.minecraftforge.common.MinecraftForge;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.fluidconverters.FluidConverters;
import org.cyclops.fluidconverters.event.TextureStitchEventHook;

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

	@Override
	public void registerEventHooks() {
		super.registerEventHooks();
		MinecraftForge.EVENT_BUS.register(new TextureStitchEventHook());
	}
}
