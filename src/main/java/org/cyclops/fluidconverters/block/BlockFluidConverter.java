package org.cyclops.fluidconverters.block;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import org.cyclops.cyclopscore.config.configurable.ConfigurableBlockContainer;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        if (itemStack != null && FluidContainerRegistry.isFilledContainer(itemStack)) {
            // Player has a container with a valid fluid: set the output of the tile entity
            Fluid fluid = FluidContainerRegistry.getFluidForFilledItem(itemStack).getFluid();
            tile.setFluidOutput(side, fluid);
        } else {
            // No valid fluid: clear the output on the given side
            tile.setFluidOutput(side, null);
        }

        // DEBUG
        player.addChatComponentMessage(new ChatComponentText("fluid group: " + tile.getFluidGroup().getGroupName()));
        for (Map.Entry<EnumFacing, Fluid> entry : tile.getFluidOutputs().entrySet()) {
            player.addChatComponentMessage(new ChatComponentText(
                    entry.getKey().toString() + ": " + entry.getValue().getName()
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
}
