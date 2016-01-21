package org.cyclops.fluidconverters.block;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.block.property.BlockProperty;
import org.cyclops.cyclopscore.block.property.UnlistedProperty;
import org.cyclops.cyclopscore.client.icon.Icon;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBlockContainer;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.fluidconverters.client.model.ModelFluidConverter;
import org.cyclops.fluidconverters.client.model.ModelFluidConverterFactory;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

import java.util.*;

/**
 * A block that converts fluids, registered as fluid groups in the FluidGroupRegistry.
 * @author immortaleeb
 */
public class BlockFluidConverter extends ConfigurableBlockContainer {

    // NBT key for the group id
    public static final String NBTKEY_GROUPID = "fluidGroupId";
    // NBT key prefix for a side
    public static final String NBTKEY_FLUIDSIDE_PREFIX = "fluidSide.";
    // Returns the NBT key for a given fluid side
    public static final String NBT_KEY_FLUIDSIDE(EnumFacing facing) {
        return NBTKEY_FLUIDSIDE_PREFIX + facing.toString();
    }

    private static NBTTagCompound NBT_CACHE = null;

    @BlockProperty
    public static final IUnlistedProperty<Map<EnumFacing, Fluid>> FLUID_OUTPUTS =
            new UnlistedProperty("fluidOutputs", Map.class);

    @SideOnly(Side.CLIENT)
    @Icon(location = "blocks/fluidconverter_open")
    public TextureAtlasSprite fluidOpenTexture;

    @SideOnly(Side.CLIENT)
    @Icon(location = "blocks/fluidconverter_center")
    public TextureAtlasSprite fluidCenterTexture;

    private static BlockFluidConverter _instance = null;

    /**
     * Get the unique instance of this class.
     *
     * @return The unique instance.
     */
    public static BlockFluidConverter getInstance() {
        return _instance;
    }

    /**
     * Make a new fluid converter block instance.
     *
     * @param eConfig    Config for this blockState.
     */
    public BlockFluidConverter(ExtendedConfig eConfig) {
        super(eConfig, Material.iron, TileFluidConverter.class);
        // Listen to ModelBakeEvents
        MinecraftForge.EVENT_BUS.register(this);
        if(MinecraftHelpers.isClientSide()) {
            eConfig.getMod().getIconProvider().registerIconHolderObject(this);
        }
    }

    private void addFluidGroupInfo(ItemStack itemStack, FluidGroup fluidGroup) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) tagCompound = new NBTTagCompound();
        tagCompound.setString(NBTKEY_GROUPID, fluidGroup.getGroupId());
        itemStack.setTagCompound(tagCompound);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup fluidGroup = it.next();
            ItemStack itemStack = new ItemStack(this);
            addFluidGroupInfo(itemStack, fluidGroup);
            list.add(itemStack);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState blockState, EntityLivingBase entity, ItemStack stack) {
        TileEntity tile = world.getTileEntity(blockPos);
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tile != null && tagCompound != null) {
            ((TileFluidConverter) tile).readStateFromNBT(tagCompound);
        }
    }

    @Override
    protected void onPreBlockDestroyed(World world, BlockPos blockPos) {
        TileEntity tile = world.getTileEntity(blockPos);
        if (tile != null && tile instanceof TileFluidConverter) {
            NBT_CACHE = ((TileFluidConverter) tile).getNBTTagCompound();
        } else {
            NBT_CACHE = null;
        }
        super.onPreBlockDestroyed(world, blockPos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.inventory.getCurrentItem();
        TileFluidConverter tile = (TileFluidConverter) world.getTileEntity(pos);
        Fluid fluid = null;

        if (itemStack != null && FluidContainerRegistry.isFilledContainer(itemStack)) {
            // Player has a container with a valid fluid: set the output of the tile entity
            fluid = FluidContainerRegistry.getFluidForFilledItem(itemStack).getFluid();
        }

        // Set or clear the fluid outputs
        tile.setFluidOutput(side, fluid);

        // Force renderer, because one of the side will display a liquid icon
        world.markBlockRangeForRenderUpdate(pos, pos);

        // DEBUG
        player.addChatComponentMessage(new ChatComponentText("fluid group: " + tile.getFluidGroup().getGroupName()));
        for (Map.Entry<EnumFacing, FluidGroup.FluidElement> entry : tile.getFluidOutputs().entrySet()) {
            player.addChatComponentMessage(new ChatComponentText(
                    entry.getKey().toString() + ": " + entry.getValue().getFluid().getName()
            ));
        }

        return true;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState blockState, int fortune) {
        ItemStack itemStack = new ItemStack(getItemDropped(blockState, new Random(), fortune), 1, damageDropped(blockState));
        if (NBT_CACHE != null) {
            itemStack.setTagCompound(NBT_CACHE);
        }
        return Lists.newArrayList(itemStack);
    }

    @Override
    public boolean hasDynamicModel() {
        return true;
    }

    @Override
    public IBakedModel createDynamicModel() {
        return new ModelFluidConverterFactory();
    }

    private Map<EnumFacing, Fluid> toBlockFluidOutputs(Map<EnumFacing, FluidGroup.FluidElement> tileFluidOutputs) {
        Map<EnumFacing, Fluid> blockFluidOutputs = new TreeMap<EnumFacing, Fluid>();

        for (Map.Entry<EnumFacing, FluidGroup.FluidElement> entry : tileFluidOutputs.entrySet()) {
            blockFluidOutputs.put(entry.getKey(), entry.getValue().getFluid());
        }

        return blockFluidOutputs;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (! (state instanceof IExtendedBlockState) ) return state;
        IExtendedBlockState ret = (IExtendedBlockState) state;
        TileFluidConverter tile = TileHelpers.getSafeTile(world, pos, TileFluidConverter.class);
        if (tile == null) return state;

        Map<EnumFacing, Fluid> fluidOutputs = toBlockFluidOutputs(tile.getFluidOutputs());
        return ret.withProperty(FLUID_OUTPUTS, fluidOutputs);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        ModelResourceLocation modelResourceLocation;
        IBakedModel baseModel = (IBakedModel) event.modelRegistry.getObject(ModelFluidConverter.modelResourceLocation);
        ModelFluidConverterFactory.setBaseModel(baseModel);
        event.modelRegistry.putObject(ModelFluidConverter.modelResourceLocation, this.createDynamicModel());
    }

    /**
     * Parses fluid outputs from nbt
     * @param nbt NBT tag from which to extract fluid outputs
     * @return Fluid outputs.
     */
    public static Map<EnumFacing, Fluid> getFluidOutputsFromNBT(NBTTagCompound nbt) {
        Map<EnumFacing, Fluid> fluidOutputs = new TreeMap<EnumFacing, Fluid>();
        for (EnumFacing facing : EnumFacing.values()) {
            String fluidName = nbt.getString(NBT_KEY_FLUIDSIDE(facing));
            Fluid fluid = fluidName != null ? FluidRegistry.getFluid(fluidName) : null;
            fluidOutputs.put(facing, fluid);
        }
        return fluidOutputs;
    }
}
