package com.amuzil.omegasource.magus.skill.event;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import net.minecraft.world.entity.LivingEntity;

public class SkillTickEvent extends SkillEvent {

    public SkillTickEvent(LivingEntity entity, RadixTree tree, Skill skill) {
        super(entity, tree, skill);
    }

    public static class Start extends SkillTickEvent {

        public Start(LivingEntity entity, RadixTree tree, Skill skill) {
            super(entity, tree, skill);
        }
    }

    public static class Run extends SkillTickEvent {

        public Run(LivingEntity entity, RadixTree tree, Skill skill) {
            super(entity, tree, skill);
        }
    }

    public static class Stop extends SkillTickEvent {

        public Stop(LivingEntity entity, RadixTree tree, Skill skill) {
            super(entity, tree, skill);
        }
    }


}
