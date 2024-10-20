package com.amuzil.omegasource.magus.skill.effects;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import net.minecraft.resources.ResourceLocation;

public abstract class Effect extends Skill {
    public Effect(String modID, String name, SkillCategory category) {
        super(modID, name, category);
    }

    public Effect(String name, SkillCategory category) {
        super(name, category);
    }

    public Effect(ResourceLocation id, SkillCategory category) {
        super(id, category);
    }

    public abstract RadixTree getPath();
}
