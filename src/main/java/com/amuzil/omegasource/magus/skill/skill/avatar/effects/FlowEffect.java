package com.amuzil.omegasource.magus.skill.skill.avatar.effects;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.builders.SkillPathBuilder;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.skill.avatar.EffectSkill;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class FlowEffect extends EffectSkill {

    public FlowEffect(String name, SkillCategory category) {
        super(name, category);
        addActivationType(RadixTree.ActivationType.MULTIKEY);
    }

    @Override
    public boolean shouldStart(LivingEntity entity, RadixTree tree) {
//        RadixUtil.getLogger().debug(tree);
//        RadixUtil.getLogger().debug(super.shouldStart(entity, tree));
//        RadixUtil.getLogger().debug(getStartPaths());
        return super.shouldStart(entity, tree);
    }

    @Override
    public boolean shouldStop(LivingEntity entity, RadixTree tree) {
        return super.shouldStop(entity, tree);
    }

    @Override
    public void start(LivingEntity entity) {
        // In case of avatar, we only care about initial path.
        // List<Condition> conditions = getMultikeyConditions();
        // List<ModifierData> data = tree.getPath().getModifiers(conditions.get(0));

        RadixUtil.getLogger().debug("Success!");

        super.start(entity);
    }

    @Override
    public boolean shouldRun(LivingEntity entity, RadixTree tree) {
        // return modifier.held > 0
        return super.shouldRun(entity, tree);
    }

    @Override
    public void run(LivingEntity entity) {
        // adjust player prepared chi (getModifierData())
        super.run(entity);
        RadixUtil.getLogger().debug("Current skill: " + this);
        RadixUtil.getLogger().debug("Current player: " + entity);
    }

    @Override
    public List<ConditionPath> getStartPaths() {
        return SkillPathBuilder.getInstance()
                // Constructs  path based on given Forms/Conditions
                .path(SkillPathBuilder.toCondition(Forms.PULL, false))
//                .path(SkillPathBuilder.toCondition(Forms.STRIKE, false))
                .finalisePath()
                .build();
    }
}
