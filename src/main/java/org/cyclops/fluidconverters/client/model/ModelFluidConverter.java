package org.cyclops.fluidconverters.client.model;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
import org.cyclops.cyclopscore.client.model.DynamicItemAndBlockModel;
import org.cyclops.cyclopscore.helper.RenderHelpers;
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.fluidconverters.Reference;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupReference;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;
import org.lwjgl.util.Color;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Model for the fluid converter block.
 * @author immortaleeb
 */
public class ModelFluidConverter extends DynamicItemAndBlockModel {

    // Resource location for the block model
    public static final ModelResourceLocation blockModelResourceLocation =
            new ModelResourceLocation(Reference.prefixModId(BlockFluidConverter.getInstance()));
    // Resource location for the item model
    public static final ModelResourceLocation itemModelResourceLocation =
            new ModelResourceLocation(Reference.prefixModId(BlockFluidConverter.getInstance()), "inventory");

    // The default color, in case the average color is missing
    private static final Color DEFAULT_COLOR = new Color(255, 255, 255, 255);

    public TextureAtlasSprite texture;

    private IBakedModel baseModel;
    private Map<EnumFacing, Fluid> fluidOutputs;
    private Color averageColor;

    public ModelFluidConverter(IBakedModel baseModel) {
        super(true, false);
        this.baseModel = baseModel;
    }

    // Creates a model factory
    public ModelFluidConverter(IBakedModel baseModel, FluidGroup fluidGroup, Map<EnumFacing, Fluid> fluidOutputs, boolean item) {
        super(false, item);
        this.baseModel = baseModel;
        this.fluidOutputs = fluidOutputs;
        this.averageColor = fluidGroup == null ? DEFAULT_COLOR : fluidGroup.getAverageColor();
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        List<BakedQuad> quads = Lists.newArrayList();

        TextureAtlasSprite fluidOpen = BlockFluidConverter.getInstance().fluidOpenTexture;
        TextureAtlasSprite fluidCenter = BlockFluidConverter.getInstance().fluidCenterTexture;

        if (fluidOpen == null || fluidCenter == null)
            return quads;

        for (EnumFacing direction : EnumFacing.values()) {
            // Does this side have a fluid output?
            Fluid fluid = fluidOutputs != null ? fluidOutputs.get(direction) : null;
            TextureAtlasSprite centerTexture = fluid != null ? RenderHelpers.getFluidIcon(fluid, EnumFacing.UP) : null;

            // In case no fluid icon was found render the default center
            if (centerTexture == null) centerTexture = fluidCenter;

            // Render the default background
            addColoredBakedQuad(quads, 0, 1, 1, 0, 0, fluidOpen, averageColor, direction);
            // Render the fluid/center icon
            addBakedQuad(quads, 0.25f, 0.75f, 0.75f, 0.25f, -0.01f, centerTexture, direction.getOpposite());

        }

        return quads;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState eState = (IExtendedBlockState) state;
            FluidGroup fluidGroup = eState.getValue(BlockFluidConverter.FLUID_GROUP);
            Map<EnumFacing, Fluid> fluidOutputs = eState.getValue(BlockFluidConverter.FLUID_OUTPUTS);

            if (fluidOutputs != null) {
                return new ModelFluidConverter(baseModel, fluidGroup, fluidOutputs, false);
            }
        }

        return baseModel;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
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
                return new ModelFluidConverter(baseModel, fluidGroup, fluidOutputs, true);
            }
        }

        return baseModel;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return RenderHelpers.getBlockIcon(Blocks.IRON_BLOCK);
    }

}
