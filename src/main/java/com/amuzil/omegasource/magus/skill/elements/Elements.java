package com.amuzil.omegasource.magus.skill.elements;

import java.util.ArrayList;
import java.util.List;

public class Elements {
    public static final List<Element> ELEMENTS = new ArrayList<>();

    public static final Element AIR = new Element("air");
    public static final Element WATER = new Element("water");
    public static final Element EARTH = new Element("earth");
    public static final Element FIRE = new Element("fire");

    public static Element fromName(String name) {
        return ELEMENTS.stream().filter(element -> element.name().equals(name)).findFirst().get();
    }
}
