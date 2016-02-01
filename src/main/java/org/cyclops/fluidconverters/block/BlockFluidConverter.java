package org.cyclops.fluidconverters.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.fluidconverters.client.model.ModelFluidConverter;
import org.cyclops.fluidconverters.client.model.ModelFluidConverterFactory;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupReference;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A block that converts fluids, registered as fluid groups in the FluidGroupRegistry.
 * @author immortaleeb
 */
public class BlockFluidConverter extends ConfigurableBlockContainer {

    @BlockProperty
    public static final IUnlistedProperty<FluidGroup> FLUID_GROUP =
            new UnlistedProperty("fluidGroup", FluidGroup.class);
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

        if(MinecraftHelpers.isClientSide()) {
            // Listen to ModelBakeEvents
            MinecraftForge.EVENT_BUS.register(this);
            eConfig.getMod().getIconProvider().registerIconHolderObject(this);
        }
    }

    private void addFluidGroupInfo(ItemStack itemStack, FluidGroup fluidGroup) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) tagCompound = new NBTTagCompound();

        // TODO: should not write raw data to NBT, but for now there is no better system in place
        // Write to NBT
        FluidGroupReference fluidGroupRef = new FluidGroupReference(fluidGroup);
        NBTClassType<FluidGroupReference> serializer = NBTClassType.getType(FluidGroupReference.class, fluidGroupRef);
        serializer.writePersistedField(TileFluidConverter.NBT_FLUID_GROUP_REF, fluidGroupRef, tagCompound);

        itemStack.setTagCompound(tagCompound);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup fluidGroup = it.next();
            list.add(createItemStack(fluidGroup));
        }
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

        // Fetch the fluid group
        FluidGroup fluidGroup = tile.getFluidGroup();
        if (fluidGroup == null) return true;

        // DEBUG
        player.addChatComponentMessage(new ChatComponentText("fluid group: " + fluidGroup.getGroupName()));
        for (Map.Entry<EnumFacing, Fluid> entry : tile.getFluidOutputs().entrySet()) {
            player.addChatComponentMessage(new ChatComponentText(
                    entry.getKey().toString() + ": " + entry.getValue().getName()
            ));
        }

        return true;
    }

    @Override
    public boolean hasDynamicModel() {
        return true;
    }

    @SideOnly(Side.CLIENT)
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

        FluidGroup fluidGroup = tile.getFluidGroupRef().getFluidGroup();
        Map<EnumFacing, Fluid> fluidOutputs = tile.getFluidOutputs();

        if (fluidGroup != null) ret = ret.withProperty(FLUID_GROUP, fluidGroup);
        if (fluidOutputs != null) ret = ret.withProperty(FLUID_OUTPUTS, fluidOutputs);
        return ret;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        ModelResourceLocation modelResourceLocation;
        IBakedModel baseModel = (IBakedModel) event.modelRegistry.getObject(ModelFluidConverter.blockModelResourceLocation);
        ModelFluidConverterFactory.setBaseModel(baseModel);

        IBakedModel model = this.createDynamicModel();

        // Register the block model
        event.modelRegistry.putObject(ModelFluidConverter.blockModelResourceLocation, model);
        // Register the same model as the item model
        event.modelRegistry.putObject(ModelFluidConverter.itemModelResourceLocation, model);
    }

    /**
     * Creates an item stack with a fluid converter for the given fluid group.
     * @param fluidGroup A fluid group for the fluid converter.
     * @return item stack that contains a single fluid converter for the given fluid group.
     */
    public static ItemStack createItemStack(FluidGroup fluidGroup) {
        BlockFluidConverter self = getInstance();
        ItemStack itemStack = new ItemStack(self);
        self.addFluidGroupInfo(itemStack, fluidGroup);
        return itemStack;
    }
}
