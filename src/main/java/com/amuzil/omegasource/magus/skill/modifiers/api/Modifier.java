package com.amuzil.omegasource.magus.skill.modifiers.api;

import com.amuzil.omegasource.magus.skill.forms.Form;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public final class Modifier {
    private ModifierData data;
    private final ModifierListener listener;

    public Modifier(ModifierData data, ModifierListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public Modifier copy() {
        return new Modifier(data().copy(), listener().copy());
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