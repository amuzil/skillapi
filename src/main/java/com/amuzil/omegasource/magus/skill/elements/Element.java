package com.amuzil.omegasource.magus.skill.elements;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;


public class Element extends SkillCategory {
    private final String name;
    private final Art art;

    public Element(Element.Art art) {
        this.name = art.name().toLowerCase();
        this.art = art;
        Registries.registerDiscipline(this);
    }

    public String name() {
        return name;
    }

    public Art type() {
        return art;
    }

    public enum Art {
        AIR, WATER, EARTH, FIRE
    }
}
