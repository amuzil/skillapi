package com.amuzil.omegasource.magus.skill.skill.avatar.effects;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.builders.SkillPathBuilder;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.MouseGestureModifierData;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.skill.avatar.EffectSkill;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public class FlowEffect extends EffectSkill {

    public FlowEffect(String name, SkillCategory category) {
        super(name, category);
        addActivationType(RadixTree.ActivationType.MULTIKEY);
    }

    @Override
    public HashMap<RadixTree.ActivationType, List<ConditionPath>> getActivationPaths() {
        // We're only testing multikey right now. Otherwise, we'd need to build for every distinct type.
        return SkillPathBuilder.getInstance()
                // Types it
                .type(RadixTree.ActivationType.MULTIKEY)
                // Constructs  path based on given Forms/Conditions
                .path(SkillPathBuilder.toCondition(Forms.ARC))
                // Adds the path to the finalised ConditionPath list
                .finalisePath()
                // Builds it
                .build();
    }

    @Override
    public void start(LivingEntity entity, RadixTree tree) {
        // In case of avatar, we only care about initial path.
        List<Condition> conditions = getMultikeyConditions();
        List<ModifierData> data = tree.getPath().getModifiers(conditions.get(0));

        super.start(entity, tree);
    }

    @Override
    public boolean shouldRun(LivingEntity entity, RadixTree tree) {
        // return modifier.held > 0
        return super.shouldRun(entity, tree);
    }

    @Override
    public void run(LivingEntity entity, RadixTree tree) {
        // adjust player prepared chi (getModifierData())
        super.run(entity, tree);
        RadixUtil.getLogger().debug("Current skill: " + this);
        RadixUtil.getLogger().debug("Current player: " + entity);
    }
}
