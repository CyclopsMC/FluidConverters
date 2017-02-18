package org.cyclops.fluidconverters.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.cyclopscore.command.CommandMod;
import org.cyclops.cyclopscore.init.ModBase;

import java.util.*;

/**
 * Command that lists all fluids in the fluid registry
 * @author immortaleeb
 */
public class CommandListFluids extends CommandMod {
    public static final String NAME = "listfluids";

    public CommandListFluids(ModBase mod) {
        super(mod);
    }

    private Collection<Fluid> getFluids() {
        return FluidRegistry.getRegisteredFluids().values();
    }

    private Fluid getFluidByName(String name) {
        return FluidRegistry.getFluid(name);
    }

    private String getFluidName(Fluid fluid) {
        return fluid.getLocalizedName(new FluidStack(fluid, 1000));
    }

    private String getFluidInfo(Fluid fluid) {
        return fluid.getName() + " (" + getFluidName(fluid) + ")";
    }

    private void printMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

    private String join(Collection collection, String delim) {
        StringBuilder builder = new StringBuilder();
        Iterator it = collection.iterator();
        if (it.hasNext()) {
            builder.append(it.next().toString());
            while (it.hasNext()) {
                builder.append(delim);
                builder.append(it.next());
            }
        }
        return builder.toString();
    }

    @Override
    protected Map<String, ICommand> getSubcommands() {
        HashMap<String, ICommand> map = Maps.newHashMap();
        for (Fluid fluid : getFluids()) {
            map.put(fluid.getName(), this);
        }
        return map;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
        List<String> info = Lists.newArrayList();

        if (astring.length == 1) {
            // Try to find a fluid with the given name
            String name = astring[0];
            Fluid fluid = getFluidByName(name);
            if (fluid != null)
                info.add(getFluidInfo(fluid));
            else
                info.add("No fluid matching '" + name + "' could be found");
        } else {
            // Loop over all fluids in the fluid registry and print their information
            for (Fluid fluid : getFluids()) {
                info.add(getFluidInfo(fluid));
            }
        }

        icommandsender.sendMessage(new TextComponentString(join(info, ", ")));
    }
}
