package com.amuzil.omegasource.magus;

import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.input.MouseMotionModule;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.test.avatar.AvatarCommand;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file

/**
 * Note! This is a separate repository for a feature of Magus. As such,
 * this file will be condensed into Magus', the modid will be changed, e.t.c.
 * This is so Mahtaran and I can work on features of Magus separately.
 */

@Mod(Magus.MOD_ID)
public class Magus {
    //MODID reference
    public static final String MOD_ID = "magus";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //todo: move these to a better place
    //todo: make multiple input modules
    public static InputModule keyboardMouseInputModule;
//    public static InputModule mouseInputModule;
    public static InputModule mouseMotionModule;

    public Magus() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        //Register the input modules
        keyboardMouseInputModule = new KeyboardMouseInputModule();
//        mouseInputModule = new MouseInputModule();
        mouseMotionModule = new MouseMotionModule();
        // Register capabilities
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CapabilityHandler::registerCapabilities);
        // attach capabilities
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityHandler::attachEntityCapability);
        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    }

    private void setup(final FMLCommonSetupEvent event) {
        // some pre init code
        Registries.init();
        CapabilityHandler.initialiseCaps();
        MagusNetwork.registerMessages();
        Forms.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //todo call this anytime the key mappings are updated
        //Assign input data to forms
        FormDataRegistry.init();
        ModifiersRegistry.init();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        LOGGER.info("Setting up Avatar commands...");
        AvatarCommand.register(event.getServer().getCommands().getDispatcher());
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
//	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//	public static class RegistryEvents {
//		@SubscribeEvent
//		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
//			// register a new block here
//			LOGGER.info("HELLO from Register Block");
//		}
//	}

    //Copied for 1.19
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");

            KeyboardMouseInputModule.determineMotionKeys();

            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    // Send a message to in-game chat
    public static void sendDebugMsg(String msg) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            System.err.println("sendDebugMsg failed: Minecraft instance is null");
            return;
        }
        minecraft.execute(() -> {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                Component text = Component.literal(msg);
                player.sendSystemMessage(text);
            } else {
                System.err.println("sendDebugMsg failed: player is null");
            }
        });
    }
}
