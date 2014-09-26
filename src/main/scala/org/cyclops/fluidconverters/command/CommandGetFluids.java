package org.cyclops.fluidconverters.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Command to get fluids.
 * @author rubensworks
 */
public class CommandGetFluids implements ICommand {

    private Collection<Fluid> getFluids() {
        return FluidRegistry.getRegisteredFluids().values();
    }

    @Override
    public String getCommandName() {
        return "getfluids";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        StringBuilder builder = new StringBuilder();
        for(Fluid fluid : getFluids()) {
            builder.append(fluid.getName());
        }
        return builder.toString();
    }

    @Override
    public List getCommandAliases() {
        return new LinkedList<String>();
    }

    protected String getFluidName(Fluid fluid) {
        return fluid.getLocalizedName(new FluidStack(fluid, 1));
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] astring) {
        if(astring.length == 1) {
            Fluid fluid = FluidRegistry.getFluid(astring[0]);
            if(fluid != null) {
                commandSender.addChatMessage(
                        new ChatComponentText(getFluidName(fluid) + ": " + fluid.getName()));
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender commandSender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] astring) {
        List list = new LinkedList<String>();
        for(Fluid fluid : getFluids()) {
            if(astring.length == 0 || fluid.getName().startsWith(astring[0])) {
                list.add(fluid.getName());
            }
        }
        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] commandSender, int i) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
