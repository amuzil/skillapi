package com.amuzil.omegasource.magus.skill.skill.avatar;

import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillActive;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EffectSkill extends SkillActive {
    public EffectSkill(String name, SkillCategory category) {
        super(name, category);
    }



    @Override
    public void start(LivingEntity entity, RadixTree tree) {
        super.start(entity, tree);
    }

    @Override
    public void run(LivingEntity entity, RadixTree tree) {

    }

    @Override
    public void stop(LivingEntity entity, RadixTree tree) {

    }
}
