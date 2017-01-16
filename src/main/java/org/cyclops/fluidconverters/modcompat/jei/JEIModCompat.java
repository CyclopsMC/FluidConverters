package org.cyclops.fluidconverters.modcompat.jei;

import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.fluidconverters.FluidConverters;
import org.cyclops.fluidconverters.Reference;

/**
 * Config for the JEI integration of this mod.
 * @author runesmacher
 *
 */
public class JEIModCompat implements IModCompat {

	/**
	 * If the modcompat can be used.
	 */
	public static boolean canBeUsed = false;

	@Override
	public void onInit(Step initStep) {
		if(initStep == Step.PREINIT) {
			canBeUsed = FluidConverters._instance.getModCompatLoader().shouldLoadModCompat(this);
		}
	}

	@Override
	public String getModID() {
		return Reference.MOD_JEI;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getComment() {
		return "Integration for Fluid Converter recipes.";
	}

}
