package com.amuzil.omegasource.magus;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressedCondition;
import com.amuzil.omegasource.magus.skill.activateable.key.KeyInput;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

import static com.amuzil.omegasource.magus.radix.condition.util.ConditionConverter.keyToConditions;

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

	public Magus() {
		// Register the setup method for mod loading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for mod loading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for mod loading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// Register the doClientStuff method for mod loading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		// some pre init code
		LOGGER.info("HELLO FROM PRE INIT");
		LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());


		//Testing for some conditions

		//Note: this is only executed client-side, due to how events work. Be sure to send a packet!
		Condition test = keyToConditions(
            new KeyInput(InputConstants.getKey(-1, InputConstants.KEY_X), 0, 0, 40)
        ).get(0);
		Condition wait = new KeyPressedCondition(40);
		wait.register(() -> {
			System.out.println("Success??");
			wait.unregister();
		}, () -> {});

		//More testing:
//        KeyInfo k1 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_D), 10, 10),
//                k2 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_D)),
//                k3 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_S), 0, 5),
//                k4 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_A)),
//                k5 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_A), 0, 2),
//                k6 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_S), 15, 0),
//                k7 = new KeyInfo(InputConstants.getKey(-1, InputConstants.KEY_W));
//
//        LinkedList<KeyInfo> ls1 = new LinkedList<>(),
//                ls2 = new LinkedList<>();
//
//        ls1.add(k2);
//        ls1.add(k3);
//        ls2.add(k5);
//        ls2.add(k6);
//        KeyPermutation p1 = new KeyPermutation(k1),
//                p2 = new KeyPermutation(ls1),
//                p3 = new KeyPermutation(k4),
//                p4 = new KeyPermutation(ls2),
//                p5 = new KeyPermutation(k7);
//
//        LinkedList<KeyPermutation> kpLs = new LinkedList<>();
//        kpLs.add(p1);
//        kpLs.add(p2);
//        kpLs.add(p3);
//        kpLs.add(p4);
//        kpLs.add(p5);
//
//        KeyCombination comb = new KeyCombination(kpLs);
//        LinkedList<Condition> conds = keysToConditions(comb);
//
//        for (Condition c : conds)
//            System.out.println(c);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
		InterModComms.sendTo("magus", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
		LOGGER.info("Got IMC {}",
			event.getIMCStream().map(message -> message.messageSupplier().get()).collect(Collectors.toList())
		);
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLDedicatedServerSetupEvent event) {
		// do something when the server starts
		LOGGER.info("HELLO from server starting");
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// register a new block here
			LOGGER.info("HELLO from Register Block");
		}
	}
}
