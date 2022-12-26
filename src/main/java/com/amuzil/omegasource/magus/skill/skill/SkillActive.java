package com.amuzil.omegasource.magus.skill.skill;

public class SkillActive extends Skill {

    public SkillActive(String name, SkillCategory category) {
        super(name, category);
    }

    //Need to account for the different types as worked out by Maht and I (FavouriteDragon).
    public boolean execute() {
        return false;
    }
}
