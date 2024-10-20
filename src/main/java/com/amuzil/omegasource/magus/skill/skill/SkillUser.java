package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Magus.MOD_ID)
public abstract class SkillUser {

    public abstract Data getData();
    public abstract LivingEntity getEntity();

    public void onUpdate() {
    }

    public void executeSkill() {
    }

    public static SkillUser getSkillUser(LivingEntity entity) {
        return null;
    }

    // TODO: Check capabilities for the entity before returning true
    public static boolean isSkillUserSupported(LivingEntity entity) {
        return false;
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        //Call onUpdate here
        if (getSkillUser(event.getEntity()) != null)
            getSkillUser(event.getEntity()).onUpdate();
    }

}
