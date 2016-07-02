package org.cyclops.fluidconverters;

import com.google.common.collect.Maps;
import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.command.CommandMod;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.config.extendedconfig.BlockItemConfigReference;
import org.cyclops.cyclopscore.init.ItemCreativeTab;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.init.RecipeHandler;
import org.cyclops.cyclopscore.modcompat.ModCompatLoader;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.fluidconverters.block.BlockFluidConverterConfig;
import org.cyclops.fluidconverters.command.CommandListFluids;
import org.cyclops.fluidconverters.fluidgroup.FluidGroup;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupRegistry;
import org.cyclops.fluidconverters.fluidgroup.FluidGroupsLoader;
import org.cyclops.fluidconverters.modcompat.capabilities.WorkerFluidConverterTileCompat;
import org.cyclops.fluidconverters.modcompat.waila.WailaModCompat;
import org.cyclops.fluidconverters.tileentity.TileFluidConverter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The main mod class of this mod.
 * @author immortaleeb
 *
 */
@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        useMetadata = true,
        version = Reference.MOD_VERSION,
        dependencies = Reference.MOD_DEPENDENCIES,
        guiFactory = "org.cyclops.fluidconverters.GuiConfigOverview$ExtendedConfigGuiFactory"
)
public class FluidConverters extends ModBaseVersionable {

    private FluidGroupsLoader fluidGroupsLoader;
    
    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     * @see SidedProxy
     */
    @SidedProxy(clientSide = "org.cyclops.fluidconverters.proxy.ClientProxy", serverSide = "org.cyclops.fluidconverters.proxy.CommonProxy")
    public static ICommonProxy proxy;
    
    /**
     * The unique instance of this mod.
     */
    @Instance(value = Reference.MOD_ID)
    public static FluidConverters _instance;

    public FluidConverters() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
    }

    @Override
    protected ICommand constructBaseCommand() {
        HashMap<String, ICommand> map = Maps.<String, ICommand>newHashMap();
        map.put(CommandListFluids.NAME, new CommandListFluids(this));
        return new CommandMod(this, map);
    }

    @Override
    protected RecipeHandler constructRecipeHandler() {
        return new FluidConvertersRecipeHandler(this);
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);

        // Mods
        modCompatLoader.addModCompat(new WailaModCompat());

        // Capabilities
        getCapabilityConstructorRegistry().registerTile(TileFluidConverter.class, new WorkerFluidConverterTileCompat());
    }

    /**
     * The pre-initialization, will register required configs.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Create the fluid groups loader
        try {
            fluidGroupsLoader = new FluidGroupsLoader(new File(event.getModConfigurationDirectory(), Reference.MOD_ID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Register the config dependent things like world generation and proxy handlers.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        // Load all fluid groups
        List<FluidGroup> fluidGroups = fluidGroupsLoader.load();
        // Add them to the registry
        FluidGroupRegistry.registerFluidGroupList(fluidGroups);

        // Print some basic info
        Iterator<FluidGroup> it = FluidGroupRegistry.iterator();
        while (it.hasNext()) {
            FluidGroup group = it.next();
            clog("Registered fluid group '" + group.getGroupName() + "' (" + group.getGroupId() + ")");
        }

        // Call super method
        super.init(event);
    }
    
    /**
     * Register the event hooks.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    
    /**
     * Register the things that are related to server starting, like commands.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    /**
     * Register the things that are related to server starting.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    /**
     * Register the things that are related to server stopping, like persistent storage.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return new ItemCreativeTab(this, new BlockItemConfigReference(BlockFluidConverterConfig.class));
    }

    @Override
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new GeneralConfig());
    }

    @Override
    public void onMainConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new BlockFluidConverterConfig());
    }

    @Override
    public ICommonProxy getProxy() {
        return proxy;
    }

    /**
     * Log a new info message for this mod.
     * @param message The message to show.
     */
    public static void clog(String message) {
        clog(Level.INFO, message);
    }
    
    /**
     * Log a new message of the given level for this mod.
     * @param level The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(Level level, String message) {
        FluidConverters._instance.getLoggerHelper().log(level, message);
    }
    
}
