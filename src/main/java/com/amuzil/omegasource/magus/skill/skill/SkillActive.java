package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Magi;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SkillActive extends Skill {

    public SkillActive(String name, SkillCategory category) {
        super(name, category);
    }

    @Override
    public List<ConditionPath> getStartPaths() {
        return null;
    }

    @Override
    public List<ConditionPath> getRunPaths() {
        return List.of();
    }

    @Override
    public List<ConditionPath> getStopPaths() {
        return List.of();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, RadixTree tree) {
        boolean canStart = false;
        RadixTree.ActivationType currentType = null;
        if (getStopPaths() == null || tree == null || tree.getPath() == null)
            return false;


        // Initialise paths here, to reduce memory consumption
        List<ConditionPath> paths = getStartPaths();
        // Needs to be changed to a search
        for (ConditionPath conditions : paths) {
            if (tree.search(conditions.conditions) != null) {
                canStart = true;
            }
        }

        Magi magi = Magi.get(entity);
        if (magi != null) {
            canStart &= magi.getSkillData(this).getState() == SkillState.START;
        } else canStart = false;

        // Finds the highest priority type that is valid, and goes with that.
        this.activatedType = currentType;
        return canStart;
    }

    @Override
    public boolean shouldRun(LivingEntity entity, RadixTree tree) {
        SkillData data;
        Magi magi = Magi.get(entity);
        if (magi != null) {
            data = magi.getSkillData(this);
            return data.getState() == SkillState.RUN;
        }
        return false;
    }

    @Override
    public boolean shouldStop(LivingEntity entity, RadixTree tree) {
        SkillData data;
        Magi magi = Magi.get(entity);
        if (magi != null) {
            data = magi.getSkillData(this);
            return data.getState() == SkillState.STOP;
        }
        return false;
    }

    @Override
    public void start(LivingEntity entity) {
        SkillData data;
        Magi magi = Magi.get(entity);
        if (magi != null) {
            data = magi.getSkillData(this);
            data.setState(SkillState.RUN);
        }
    }

    @Override
    public void run(LivingEntity entity) {

    }

    @Override
    public void stop(LivingEntity entity) {

    }

    @Override
    public void reset(LivingEntity entity, RadixTree tree) {

    }

    //Need to account for the different types as worked out by Maht and I (FavouriteDragon).
    public boolean execute() {
        return false;
    }


}
