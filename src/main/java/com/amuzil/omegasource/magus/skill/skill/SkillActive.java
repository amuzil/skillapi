package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public class SkillActive extends Skill {

    public SkillActive(String name, SkillCategory category) {
        super(name, category);
    }

    @Override
    public HashMap<RadixTree.ActivationType, List<ConditionPath>> getActivationPaths() {
        return null;
    }


    @Override
    public boolean shouldStart(LivingEntity entity, RadixTree tree) {
        boolean canStart = false;
        RadixTree.ActivationType currentType = null, prevType = null;
        if (getActivationPaths() == null || tree == null || tree.getPath() == null)
            return false;

        // Initialise paths here, to reduce memory consumption
        HashMap<RadixTree.ActivationType, List<ConditionPath>> paths = getActivationPaths();
        for (RadixTree.ActivationType type : getActivationTypes()) {
            // Needs to be changed to a search
            for (ConditionPath conditions : paths.get(type)) {
                if (tree.search(conditions.conditions) != null) {
                    canStart = true;
                    currentType = type;
                    if (prevType != null && prevType.priority() > currentType.priority()) {
                        currentType = prevType;
                    } else prevType = currentType;
                }
            }
        }

        // Finds the highest priority type that is valid, and goes with that.
        this.activatedType = currentType;
        return canStart && state == SkillState.START;
    }

    @Override
    public boolean shouldRun(LivingEntity entity, RadixTree tree) {
        return state == SkillState.RUN;
    }

    @Override
    public boolean shouldStop(LivingEntity entity, RadixTree tree) {
        return state == SkillState.STOP;
    }

    @Override
    public void start(LivingEntity entity, RadixTree tree) {
        this.state = SkillState.RUN;
    }

    @Override
    public void run(LivingEntity entity, RadixTree tree) {
        this.state = SkillState.STOP;
    }

    @Override
    public void stop(LivingEntity entity, RadixTree tree) {
        this.state = SkillState.START;
    }

    @Override
    public void reset(LivingEntity entity, RadixTree tree) {

    }

    //Need to account for the different types as worked out by Maht and I (FavouriteDragon).
    public boolean execute() {
        return false;
    }
}
