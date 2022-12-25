package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.registry.Registries;

import java.util.List;
import java.util.UUID;

/**
 * Basic skill class. All other skills extend this.
 */
public class Skill {

    private String name;
    private UUID category;

    //Modifier data
    //Constant data
    //E.t.c

    public static List<Skill> getSkills() {
        return (List<Skill>) Registries.SKILLS.get().getValues();
    }

}
