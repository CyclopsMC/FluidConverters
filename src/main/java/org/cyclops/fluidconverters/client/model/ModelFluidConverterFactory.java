package org.cyclops.fluidconverters.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
import org.cyclops.cyclopscore.client.model.DynamicModel;
import org.cyclops.fluidconverters.block.BlockFluidConverter;

import java.util.Map;

/**
 * A factory that creates {@link org.cyclops.fluidconverters.client.model.ModelFluidConverter}S.
 * @author immortaleeb
 */
public class ModelFluidConverterFactory extends DynamicModel {
    // Holds the base model for a fluid converter
    private static IBakedModel baseModel;
    // Sets the base model
    public static void setBaseModel(IBakedModel model) {
        baseModel = model;
    }

    public ModelFluidConverterFactory() {
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState eState = (IExtendedBlockState) state;
            Map<EnumFacing, Fluid> fluidOutputs = eState.getValue(BlockFluidConverter.FLUID_OUTPUTS);

            if (fluidOutputs != null) {
                return new ModelFluidConverter(this, baseModel, fluidOutputs);
            }
        }

        return baseModel;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            Map<EnumFacing, Fluid> fluidOutputs = BlockFluidConverter.getFluidOutputsFromNBT(nbt);
            if (fluidOutputs != null) {
                return new ModelFluidConverter(this, baseModel, fluidOutputs);
            }
        }

        return baseModel;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }
}
