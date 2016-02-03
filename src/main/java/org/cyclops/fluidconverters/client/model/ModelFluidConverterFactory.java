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
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupReference;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

import java.util.Map;
import java.util.TreeMap;

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
            FluidGroup fluidGroup = eState.getValue(BlockFluidConverter.FLUID_GROUP);
            Map<EnumFacing, Fluid> fluidOutputs = eState.getValue(BlockFluidConverter.FLUID_OUTPUTS);

            if (fluidOutputs != null) {
                return new ModelFluidConverter(this, baseModel, fluidGroup, fluidOutputs);
            }
        }

        return baseModel;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {

            // TODO: should not read raw NBT, but for now no better system is in place to handle this
            // Read fluid group from nbt
            FluidGroupReference fluidGroupRef = new FluidGroupReference();
            NBTClassType<FluidGroupReference> fluidGroupSerializer = NBTClassType.getType(FluidGroupReference.class, fluidGroupRef);
            fluidGroupRef = fluidGroupSerializer.readPersistedField(TileFluidConverter.NBT_FLUID_GROUP_REF, nbt);
            FluidGroup fluidGroup = fluidGroupRef.getFluidGroup();

            // Read the fluid outputs from nbt
            Map<EnumFacing, Fluid> fluidOutputs = new TreeMap<EnumFacing, Fluid>();
            NBTClassType<Map> serializer = NBTClassType.getType(Map.class, fluidOutputs);
            fluidOutputs = serializer.readPersistedField(TileFluidConverter.NBT_FLUID_OUTPUTS, nbt);

            if (fluidGroup != null && fluidOutputs != null) {
                return new ModelFluidConverter(this, baseModel, fluidGroup, fluidOutputs);
            }
        }

        return baseModel;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }
}
