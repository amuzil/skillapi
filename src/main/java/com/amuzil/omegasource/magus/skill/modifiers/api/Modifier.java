package com.amuzil.omegasource.magus.skill.modifiers.api;

import net.minecraft.nbt.CompoundTag;

public final class Modifier {
    private ModifierData data;
    private final ModifierListener listener;

    public Modifier(ModifierData data, ModifierListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public Modifier copy() {
        return new Modifier(data().copy(), listener() != null? listener().copy() : null);
    }

    public void print() {
        data.print();
    }

    public ModifierData data() {
        return data;
    }

    public void setData(ModifierData data) {
        this.data = data;
    }

    public ModifierListener listener() {
        return listener;
    }

    public void setupListener(CompoundTag compoundTag) {
        listener.setupListener(compoundTag);
    }
}
