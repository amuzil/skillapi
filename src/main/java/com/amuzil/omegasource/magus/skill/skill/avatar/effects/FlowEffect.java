package com.amuzil.omegasource.magus.skill.skill.avatar.effects;

import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.builders.SkillPathBuilder;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.skill.avatar.EffectSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlowEffect extends EffectSkill {

    public FlowEffect(String name, SkillCategory category) {
        super(name, category);
    }

    @Override
    public HashMap<RadixTree.ActivationType, List<ConditionPath>> getActivationPaths() {
        // We're only testing multikey right now. Otherwise, we'd need to build for every distinct type.
        return SkillPathBuilder.getInstance().type(RadixTree.ActivationType.MULTIKEY).path(SkillPathBuilder.toCondition(Forms.ARC)).finalisePath().build();
    }
}
