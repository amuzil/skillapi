package com.amuzil.omegasource.magus.server;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Node;
import com.amuzil.omegasource.magus.radix.NodeBuilder;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.test.avatar.AvatarFormRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
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
            if (capability != null && event.getEntity() instanceof Player) {


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
                // Need to test out the condition tree. use left alt/arc > strike (left click).
                // While this test code will directly use conditions, Skills will reference Forms
                // that get automatically turned into conditions.
//                KeyInput initializer = KeyDataBuilder.createInput("key.keyboard.left.alt", 0, 0, 4);
//                KeyInput left = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyAttack.getKey(), 0,
//                        0, 4);
//
//                Condition arc = new KeyHoldCondition(initializer.key().getValue(), initializer.held(), 20000, false);
//                arc.register("Arc", arc::unregister, () -> {
//                });
//                ConditionRegistry.register(arc);

                // TODO: Fix the tree to only change immediate children's conditions,
                // and only register/unregister them in the tree itself
//                Condition strike = new KeyHoldCondition(left.key().getValue(), left.held(), 2000000, false);
//                strike.register("Strike", () -> {
////                    strike.unregister();
//                    Entity eventEntity = event.getEntity();
//
//                    Level level = event.getLevel();
//                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
//
//                    if (lightningBolt != null) {
//                        // Set the position of the lightning bolt
//                        lightningBolt.moveTo(eventEntity.xo, eventEntity.yo, eventEntity.zo);
//
//                        // Spawn the lightning bolt in the world
//                        level.addFreshEntity(lightningBolt);
//                        MagusNetwork.sendToServer(new ConditionActivatedPacket(strike));//, (ServerPlayer) eventEntity);
//
//                    }
//
//                }, () -> {
//                });
//                ConditionRegistry.register(strike);
//
//
//                Condition arc2 = new FormCondition(Forms.ARC, -1, Magus.keyboardInputModule);
//                arc2.register("ARC", () -> {
//                    Magus.sendDebugMsg("ARC FORM TRIGGERED");
//                }, () -> {});
//
//                System.out.println("Test RadixTree");
//                RadixTree tree = new RadixTree();
//                for (Condition condition : ConditionRegistry.getConditions()) {
//                    List<Condition> conditionPath = new ArrayList<>();
//                    conditionPath.add(condition);
//                    conditionPath.add(arc);
//                    tree.insert(conditionPath);
//                }
//                for (Condition condition : ConditionRegistry.getConditions()) {
//                    List<Condition> conditionPath = new ArrayList<>();
//                    conditionPath.add(condition);
//                    conditionPath.add(strike);
//                    tree.insert(conditionPath);
//                }
//                System.out.println("FormConditions List:");
//                tree.printAllConditions();
//                System.out.println("FormConditions Tree:");
//                tree.printAllBranches();
//                List<Condition> conditionPath = new ArrayList<>();
//                conditionPath.add(strike);
//                conditionPath.add(arc);
//                System.out.println("RadixTree.search passing result:\n" + tree.search(conditionPath));
//                conditionPath.clear();
//                conditionPath.add(strike);
//                System.out.println("RadixTree.search failed result:\n" + tree.search(conditionPath));
            }
        } else {
            if (event.getEntity() instanceof Player) {
                AvatarFormRegistry.registerForms();
                Magus.keyboardInputModule.init();
                Magus.mouseInputModule.init();
//                Magus.mouseMotionModule.init();
            }
        }
    }

    @SubscribeEvent
    public static void tickEvent(TickEvent.PlayerTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = event.player;
        // Get the current mouse X and Y positions
        double mouseX = minecraft.mouseHandler.xpos();
        double mouseY = minecraft.mouseHandler.ypos();

        // Print the mouse coordinates
//        System.out.println("Mouse X: " + mouseX + ", Mouse Y: " + mouseY);
//        if (player.tickCount % 10 == 0) {
//            System.out.println("Mouse X : " + mouseX + ", Mouse Y: " + mouseY);
//        }

    }

    @SubscribeEvent
    public static void OnPlayerLeaveWorld(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() instanceof Player) {
            Magus.keyboardInputModule.terminate();
            Magus.mouseInputModule.terminate();
            Magus.mouseMotionModule.terminate();
        }
    }
}
