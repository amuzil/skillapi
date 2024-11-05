package com.amuzil.omegasource.magus.level.event;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Magi;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Magus.MOD_ID)
public class MagusEvents {

//    @SubscribeEvent
//    public static void tickEvent(LivingEvent.LivingTickEvent event) {
//        if (event.getEntity() != null) {
//            Magi magi = Magi.get(event.getEntity());
//            if (magi != null) {
//                magi.onUpdate();
//            }
//        }
//    }

    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        if (event.getEntity() != null) {
            Magi magi = Magi.get(event.getEntity());
            if (magi != null) {
                magi.onDeath();
            }
        }
    }
}
