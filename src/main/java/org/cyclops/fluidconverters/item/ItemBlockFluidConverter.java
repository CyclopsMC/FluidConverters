package org.cyclops.fluidconverters.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.item.ItemBlockNBT;
import org.cyclops.fluidconverters.block.BlockFluidConverter;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;

import java.util.List;

/**
 * Item class for {@link org.cyclops.fluidconverters.block.BlockFluidConverter}.
 * @author immortaleeb
 */
public class ItemBlockFluidConverter extends ItemBlockNBT {

    public ItemBlockFluidConverter(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);

        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt != null) {
            FluidGroup fluidGroup = BlockFluidConverter.getFluidGroupFromNBT(nbt);
            list.add(EnumChatFormatting.GOLD +
                L10NHelpers.localize("tile.blocks.fluidConverter.converter") + ": " +
                fluidGroup.getGroupName()
            );

            if (MinecraftHelpers.isShifted()) {
                for (FluidGroup.FluidElement fluidElement : fluidGroup.getFluidElements()) {
                    list.add(EnumChatFormatting.GRAY +
                        fluidElement.getFluid().getLocalizedName(null) + ": " +
                        "%.2f".format(fluidElement.getValue() + "")
                    );
                }
            } else {
                list.add("" + EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC +
                    L10NHelpers.localize("general.cyclopscore.tooltip.info"));
            }
        }
    }
}
