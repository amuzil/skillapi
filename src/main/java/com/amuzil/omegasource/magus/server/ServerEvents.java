package com.amuzil.omegasource.magus.server;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.*;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.test.avatar.AvatarFormRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                KeyInput initialiser = KeyDataBuilder.createInput("key.keyboard.left.alt", 0, 0, 1);
                KeyInput left = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyAttack.getKey(), 0,
                        0, 1);

                Condition arc = new KeyHoldCondition(initialiser.key().getValue(), initialiser.held(), 20000, false);
                Condition strike = new KeyHoldCondition(left.key().getValue(), left.held(), 2000000, false);
                strike.register(() -> {
                    Entity eventEntity = event.getEntity();
                    RadixUtil.getLogger().debug("Working strike??");

                    Level level = event.getLevel();
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);

                    if (lightningBolt != null) {
                        // Set the position of the lightning bolt
                        lightningBolt.moveTo(eventEntity.xo, eventEntity.yo, eventEntity.zo);

                        // Spawn the lightning bolt in the world
                        level.addFreshEntity(lightningBolt);
                    }

                    }, () -> {});
                Node node2 = NodeBuilder.middle().addChild(strike, NodeBuilder.end().build()).build();

                RadixTree tree = new RadixTree(NodeBuilder.root().addChild(arc, node2).build());
//
//                //todo this is not be where we should call start, but for now it'll stop us crashing until
//                // we have a key for activating the bending state
                tree.setOwner(event.getEntity());
                capability.setTree(tree);
                capability.getTree().start();
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
