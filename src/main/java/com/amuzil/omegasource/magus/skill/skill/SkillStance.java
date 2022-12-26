package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.radix.RadixTree;
import net.minecraft.world.entity.LivingEntity;

/**
 * Name WIP.
 * Functions as a "stance" skill, as described in Avatar's documentation, design, and outline.
 * Stance skills are in between active and passive skills. You have to activate them, but rather than
 * producing a direct effect, they invoke a passive effect upon the player.
 * The idea is to have a cycle method or something similar to rotate through unlocked stances.
 * Stance skills could range from Sun stance firebending, to give access to new combos,
 * to different types of martial arts in a martial arts mod. E.g variations on karate.
 */
public class SkillStance extends Skill {
    public SkillStance(String name, SkillCategory category) {
        super(name, category);
    }

    @Override
    public boolean shouldStart(LivingEntity entity, RadixTree tree) {
        return false;
    }

    @Override
    public boolean shouldRun(LivingEntity entity, RadixTree tree) {
        return false;
    }

    @Override
    public boolean shouldStop(LivingEntity entity, RadixTree tree) {
        return false;
    }

    @Override
    public void start(LivingEntity entity, RadixTree tree) {

    }

    @Override
    public void run(LivingEntity entity, RadixTree tree) {

    }

    @Override
    public void stop(LivingEntity entity, RadixTree tree) {

    }
}
