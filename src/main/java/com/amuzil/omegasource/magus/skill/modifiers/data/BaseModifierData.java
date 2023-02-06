package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;

public abstract class BaseModifierData extends ModifierData {

    private boolean isDirty = false;

    @Override
    public void markDirty() {
        isDirty = true;
    }

    @Override
    public void markClean() {
        isDirty = false;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        return compoundTag;
    }
}
