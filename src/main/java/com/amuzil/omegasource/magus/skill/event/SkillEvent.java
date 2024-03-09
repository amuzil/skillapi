package com.amuzil.omegasource.magus.skill.event;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class SkillEvent extends LivingEvent {

    private Skill skill;
    private RadixTree tree;

    public SkillEvent(LivingEntity entity, RadixTree tree, Skill skill) {
        super(entity);
        this.skill = skill;
        this.tree = tree;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public Skill getSkill() {
        return skill;
    }

    public RadixTree getTree() {
        return tree;
    }
}
