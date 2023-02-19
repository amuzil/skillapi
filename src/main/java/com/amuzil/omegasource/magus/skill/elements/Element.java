package com.amuzil.omegasource.magus.skill.elements;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;

public class Element extends SkillCategory {
    private final String name;

    public Element(String name) {
        this.name = name;
        Registries.registerElement(this);
    }

    public String name() {
        return name;
    }
}
