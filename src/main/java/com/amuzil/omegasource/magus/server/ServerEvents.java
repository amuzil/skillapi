package com.amuzil.omegasource.magus.server;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.ConditionActivatedPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.Node;
import com.amuzil.omegasource.magus.radix.NodeBuilder;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.test.avatar.AvatarFormRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEvents {

    @SubscribeEvent
    public static void worldStart(LevelEvent event) {

    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {

        if (!event.getLevel().isClientSide()) {
            Data capability = CapabilityHandler.getCapability(event.getEntity(), CapabilityHandler.LIVING_DATA);
            if (capability != null) {


                // initialise the radix tree and set the player as an instance property for sending packets.
                //todo this is temporary manual tree construction for testing purposes. the true tree will be
                // generated at runtime based on available skills for the player/entity.
                Node secondNode = NodeBuilder.middle()
                        .addModifiers(ModifiersRegistry.FOCUS.copy(), ModifiersRegistry.MULTI.copy(),
                                ModifiersRegistry.DIRECTION.copy(), ModifiersRegistry.TARGET.copy())
                        .build();
                //Resets the tree; for testing purposes.
                if (capability.getTree() != null)
                    capability.getTree().burn();
                // TODO: Need a way to convert forms into conditions
//                RadixTree tree = new RadixTree(NodeBuilder.root().addChildren(new Pair<>(Forms.ARC, secondNode),
//                        new Pair<>(Forms.STEP, secondNode)).build());
//                // new Pair<>(Forms.FORCE, secondNode),
////                        new Pair<>(Forms.BURST, secondNode)).build());
//                tree.setOwner(event.getEntity());
//                capability.setTree(tree);
                // Need to test out the condition tree. use left alt/arc > strike (left click).
                // While this test code will directly use conditions, Skills will reference Forms
                // that get automatically turned into conditions.
                KeyInput initialiser = KeyDataBuilder.createInput("key.keyboard.left.alt", 0, 0, 4);
                KeyInput left = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyAttack.getKey(), 0,
                        0, 4);

                Condition arc = new KeyHoldCondition(initialiser.key().getValue(), initialiser.held(), 20000, false);
                arc.register(() -> {
                }, () -> {
                });
                arc.unregister();
                arc.name("arc");
                arc.registerEntry();

                Condition strike = new KeyHoldCondition(left.key().getValue(), left.held(), 2000000, false);
                // TODO: Fix the tree to only change immediate children's conditions,
                // and only register/unregister them in the tree itself
//                strike.name("strike");
//                strike.registerEntry();
                strike.register(() -> {
                    Entity eventEntity = event.getEntity();

                    Level level = event.getLevel();
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);

                    if (lightningBolt != null) {
                        // Set the position of the lightning bolt
                        lightningBolt.moveTo(eventEntity.xo, eventEntity.yo, eventEntity.zo);

                        // Spawn the lightning bolt in the world
                        level.addFreshEntity(lightningBolt);
//                        if (eventEntity instanceof ServerPlayer)
                            MagusNetwork.sendToServer(new ConditionActivatedPacket(strike));//, (ServerPlayer) eventEntity);
                        strike.unregister();
                    }

                }, () -> {
                });
//                strike.unregister();

                Node root = NodeBuilder.root().build();
                Node middle = NodeBuilder.middle().addParent(new Pair<>(root.terminateCondition(), root)).build();
                Node end = NodeBuilder.end().addParent(new Pair<>(arc, middle)).build();
                middle = middle.children().put(strike, end);
                root = root.children().put(arc, middle);
//
//                RadixTree tree = new RadixTree(root);
//
//                //todo this is not be where we should call start, but for now it'll stop us crashing until
//                // we have a key for activating the bending state
//                tree.setOwner(event.getEntity());
//                capability.setTree(tree);
//                capability.getTree().start();
            }
        } else {
            if (event.getEntity() instanceof Player) {
                ((KeyboardMouseInputModule) Magus.keyboardInputModule).resetKeys();
                Magus.keyboardInputModule.registerListeners();
                AvatarFormRegistry.registerForms();
            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerLeaveWorld(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() instanceof Player) {
            Magus.keyboardInputModule.unregisterInputs();
            ((KeyboardMouseInputModule) Magus.keyboardInputModule).resetKeys();
        }
    }
}
