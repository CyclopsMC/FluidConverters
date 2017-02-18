package org.cyclops.fluidconverters.modcompat.jei.fluidconverter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup.FluidElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Recipe wrapper for Fluid converter recipes
 * @author runesmacher
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class FluidConverterRecipeJEI extends BlankRecipeWrapper {
    private final FluidElement fluidInput, fluidOutput;
    public final FluidStack inputStack, outputStack;

    private final FluidGroup fluidGroup;
    private Rectangle conversionRateTooltipArea;
    private Rectangle lossTooltipArea;

    public FluidConverterRecipeJEI(FluidElement fluidInput, FluidElement fluidOutput, FluidGroup fluidGroup) {
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.fluidGroup = fluidGroup;

        int inputAmount = 1000;
        int outputAmount = MathHelper.floor_float(
                fluidOutput.denormalize((1 - fluidGroup.getLossRatio()) * fluidInput.normalize(inputAmount)));

        if (outputAmount > 1000) {
            inputAmount = MathHelper.floor_float(
                    fluidInput.denormalize((1 - fluidGroup.getLossRatio()) * fluidOutput.normalize(inputAmount)));
            outputAmount = 1000;
        }

        this.inputStack = new FluidStack(fluidInput.getFluid(), inputAmount);
        this.outputStack = new FluidStack(fluidOutput.getFluid(), outputAmount);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        double ratio = Math.round(fluidInput.getValue() / fluidOutput.getValue() * 100.0) / 100.0;
        String str = String.format("x%s", ratio);
        if (str != null) {
            int stringWidth = minecraft.fontRendererObj.getStringWidth(str);
            int x = (94 - stringWidth) / 2;
            int y = 10;
            minecraft.fontRendererObj.drawString(str, x, y, 0x808080, false);

            conversionRateTooltipArea = new Rectangle(x, y, minecraft.fontRendererObj.getStringWidth(str),
                    minecraft.fontRendererObj.FONT_HEIGHT);
        }

        if (fluidGroup.getLossRatio() > 0)
            str = String.format("-%.0f%%", fluidGroup.getLossRatio() * 100);
        else
            str = "";

        if (str != null) {
            int stringWidth = minecraft.fontRendererObj.getStringWidth(str);
            int x = (94 - stringWidth) / 2;
            int y = 40;
            minecraft.fontRendererObj.drawString(str, x, y, 0x808080, false);

            lossTooltipArea = new Rectangle(x, y, minecraft.fontRendererObj.getStringWidth(str),
                    minecraft.fontRendererObj.FONT_HEIGHT);
        }
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> ret = new ArrayList<String>();
        if (conversionRateTooltipArea.contains(mouseX, mouseY)) {
            ret.add(L10NHelpers.localize("jeigui.fluidconverters.rate"));
            return ret;
        }
        if (lossTooltipArea.contains(mouseX, mouseY)) {
            ret.add(L10NHelpers.localize("jeigui.fluidconverters.loss"));
            return ret;
        }
        return null;
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (fluidInput != null) {
            ingredients.setInput(FluidStack.class, inputStack);
        }
        if (fluidOutput != null) {
            ingredients.setOutput(FluidStack.class, outputStack);
        }
    }
}
