package com.amuzil.omegasource.magus.skill.modifiers.api;

public record Modifier(ModifierData data, ModifierListener listener) {
    public Modifier copy() {
        return new Modifier(data(), listener());
    }

    public void print() {
        data.print();
    }
}
