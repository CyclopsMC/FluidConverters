package org.cyclops.fluidconverters.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.item.ItemBlockNBT;
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupReference;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

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
        if (nbt == null) return;

        // TODO: we shouldn't be parsing NBT manually here, clean this up some day
        // Fluid group
        FluidGroupReference fluidGroupRef = new FluidGroupReference();
        NBTClassType<FluidGroupReference> serializer = NBTClassType.getType(FluidGroupReference.class, fluidGroupRef);
        fluidGroupRef = serializer.readPersistedField(TileFluidConverter.NBT_FLUID_GROUP_REF, nbt);
        FluidGroup fluidGroup = fluidGroupRef.getFluidGroup();

        // Buffer
        float buffer = 0f;
        NBTClassType<Float> bufferSerializer = NBTClassType.getType(Float.class, buffer);
        buffer = bufferSerializer.readPersistedField(TileFluidConverter.NBT_BUFFER, nbt);

        addInformation(list, fluidGroup, buffer, MinecraftHelpers.isShifted());
    }

    /**
     * Adds information about a fluid converter to the given info list.
     * @param info List of already existing info lines
     * @param fluidGroup The fluid group that should be displayed
     * @param isShifted When set to false a "shift to get more info" will be shown, otherwise the
     *                  full info is shown
     */
    public static void addInformation(List info, FluidGroup fluidGroup, float buffer, boolean isShifted) {
        if (fluidGroup != null) {
            info.add(TextFormatting.GOLD +
                            L10NHelpers.localize("tile.blocks.fluidconverters.fluidconverter.converter") + ": " +
                            fluidGroup.getGroupName()
            );

            if (isShifted) {
                // Buffer info
                info.add("" + TextFormatting.DARK_GRAY +
                        "Buffer: " + buffer + " / " + TileFluidConverter.getMaxBufferSize());

                // Fluid elements
                List<FluidGroup.FluidElement> fluidElements = fluidGroup.getFluidElements();
                if (fluidElements != null) {
                    for (FluidGroup.FluidElement fluidElement : fluidElements) {
                        info.add(TextFormatting.GRAY +
                                        fluidElement.getFluid().getLocalizedName(null) + ": " +
                                        "%.2f".format(fluidElement.getValue() + "")
                        );
                    }
                }

                info.add("" + TextFormatting.DARK_GRAY +
                    "Loss rate: " + (fluidGroup.getLossRatio() * 100) + " %");
            } else {
                info.add("" + TextFormatting.GRAY + TextFormatting.ITALIC +
                        L10NHelpers.localize("general.cyclopscore.tooltip.info"));
            }
        } else {
            info.add(TextFormatting.ITALIC + L10NHelpers.localize("info.invalid"));
        }
    }
}
