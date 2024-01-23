package com.amuzil.omegasource.magus.skill.elements;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;

public class Discipline extends SkillCategory {
    private final String name;

    public Discipline(String name) {
        this.name = name;
        Registries.registerDiscipline(this);
    }

    public String name() {
        return name;
    }
}
