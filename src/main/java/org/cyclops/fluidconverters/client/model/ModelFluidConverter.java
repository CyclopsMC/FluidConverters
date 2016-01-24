package org.cyclops.fluidconverters.client.model;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import org.cyclops.cyclopscore.client.model.DynamicModel;
import org.cyclops.cyclopscore.helper.RenderHelpers;
import org.cyclops.fluidconverters.Reference;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.lwjgl.util.Color;

import java.util.List;
import java.util.Map;

/**
 * Model for the fluid converter block.
 * @author immortaleeb
 */
public class ModelFluidConverter extends DynamicModel {

    // Resource location for the block model
    public static final ModelResourceLocation blockModelResourceLocation =
            new ModelResourceLocation(Reference.prefixModId(BlockFluidConverter.getInstance()));
    // Resource location for the item model
    public static final ModelResourceLocation itemModelResourceLocation =
            new ModelResourceLocation(Reference.prefixModId(BlockFluidConverter.getInstance()), "inventory");

    public TextureAtlasSprite texture;

    private ModelFluidConverterFactory factory;
    private IBakedModel baseModel;
    private FluidGroup fluidGroup;
    private Map<EnumFacing, Fluid> fluidOutputs;

    // Creates a model factory
    public ModelFluidConverter(ModelFluidConverterFactory factory, IBakedModel baseModel, FluidGroup fluidGroup, Map<EnumFacing, Fluid> fluidOutputs) {
        this.factory = factory;
        this.baseModel = baseModel;
        this.fluidGroup = fluidGroup;
        this.fluidOutputs = fluidOutputs;
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        List<BakedQuad> quads = Lists.newArrayList();

        TextureAtlasSprite fluidOpen = BlockFluidConverter.getInstance().fluidOpenTexture;
        TextureAtlasSprite fluidCenter = BlockFluidConverter.getInstance().fluidCenterTexture;

        Color averageColor = fluidGroup.getAverageColor();

        if (fluidOpen == null || fluidCenter == null)
            return quads;

        for (EnumFacing direction : EnumFacing.values()) {
            // Does this side have a fluid output?
            Fluid fluid = fluidOutputs.get(direction);
            TextureAtlasSprite centerTexture = fluid != null ? RenderHelpers.getFluidIcon(fluid, EnumFacing.UP) : null;

            // In case no fluid icon was found render the default center
            if (centerTexture == null) centerTexture = fluidCenter;

            // Render the default background
            this.addBakedQuad(quads, 0, 1, 1, 0, 0, fluidOpen, averageColor, direction);
            // Render the fluid/center icon
            this.addBakedQuad(quads, 0.25f, 0.75f, 0.75f, 0.25f, -0.0001f, centerTexture, direction.getOpposite());

        }

        return quads;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        return factory.handleBlockState(state);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return factory.handleItemState(stack);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return RenderHelpers.getBlockIcon(Blocks.iron_block);
    }

}
