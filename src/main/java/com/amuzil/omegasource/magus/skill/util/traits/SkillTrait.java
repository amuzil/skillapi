package com.amuzil.omegasource.magus.skill.util.traits;

import net.minecraft.nbt.CompoundTag;

public class SkillTrait implements DataTrait {
    private String name;
    private boolean isDirty = false;

    public SkillTrait(String name) {
        this.name = name;
        markDirty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public void markClean() {
        this.isDirty = false;
    }

    @Override
    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(getName(), name);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        markClean();
        name = nbt.getString(getName());
    }
}
