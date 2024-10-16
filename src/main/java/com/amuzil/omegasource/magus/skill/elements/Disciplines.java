package com.amuzil.omegasource.magus.skill.elements;

import java.util.ArrayList;
import java.util.List;

public class Disciplines {
    public static final List<Discipline> DISCIPLINES = new ArrayList<>();

    public static final Discipline AIR = new Discipline("air");
    public static final Discipline WATER = new Discipline("water");
    public static final Discipline EARTH = new Discipline("earth");
    public static final Discipline FIRE = new Discipline("fire");

    public static Discipline fromName(String name) {
        return DISCIPLINES.stream().filter(element -> element.name().equals(name)).findFirst().get();
    }
}
