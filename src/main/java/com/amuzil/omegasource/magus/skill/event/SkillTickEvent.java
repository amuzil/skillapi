package com.amuzil.omegasource.magus.skill.event;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.forms.ActiveForm;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SkillTickEvent extends SkillEvent {

    public SkillTickEvent(LivingEntity entity, List<ActiveForm> formPath, Skill skill) {
        super(entity, formPath, skill);
    }

    public static class Start extends SkillTickEvent {

        public Start(LivingEntity entity, List<ActiveForm> formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }

    public static class Run extends SkillTickEvent {

        public Run(LivingEntity entity, List<ActiveForm> formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }

    public static class Stop extends SkillTickEvent {

        public Stop(LivingEntity entity, List<ActiveForm> formPath, Skill skill) {
            super(entity, formPath, skill);
        }
    }


}
