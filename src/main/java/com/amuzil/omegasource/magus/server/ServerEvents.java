package com.amuzil.omegasource.magus.server;

import com.amuzil.omegasource.magus.radix.Node;
import com.amuzil.omegasource.magus.radix.NodeBuilder;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import com.amuzil.omegasource.magus.skill.util.capability.entity.LivingDataCapability;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide()) {
            Data capability = CapabilityHandler.getCapability(event.getEntity(), CapabilityHandler.LIVING_DATA);
            if(capability != null) {

                // initialise the radix tree and set the player as an instance property for sending packets.
                //todo this is temporary manual tree construction for testing purposes. the true tree will be
                // generated at runtime based on available skills for the player/entity.
                Node secondNode = NodeBuilder.middle()
                        .addModifier(ModifiersRegistry.FOCUS.copy())
                        .addModifier(ModifiersRegistry.MULTI.copy())
                        .addModifier(ModifiersRegistry.DIRECTION.copy())
                        .addModifier(ModifiersRegistry.TARGET.copy())
                        .build();
                RadixTree tree = new RadixTree(NodeBuilder.root().addChild(Forms.STRIKE, secondNode).build());
                tree.setOwner(event.getEntity());
                capability.setTree(tree);

                //todo this is not be where we should call start, but for now it'll stop us crashing until
                // we have a key for activating the bending state
                capability.getTree().start();
            }
        }
    }
}
