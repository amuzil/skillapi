package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.radix.RadixTree;
import net.minecraft.world.entity.LivingEntity;

public class SkillActive extends Skill {

    public SkillActive(String name, SkillCategory category) {
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

    //Need to account for the different types as worked out by Maht and I (FavouriteDragon).
    public boolean execute() {
        return false;
    }
}
