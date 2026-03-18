package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.elements.Elements;
import com.amuzil.omegasource.magus.skill.skill.avatar.effects.FlowEffect;

import java.util.ArrayList;
import java.util.List;

public class Skills {
    public static void registerSkills() {
        List<Skill> skills = new ArrayList<>();

        // TODO: Move registration into skill categories
        skills.add(new FlowEffect("air_flow", Elements.AIR));


        Registries.registerSkills(skills);
    }
}
