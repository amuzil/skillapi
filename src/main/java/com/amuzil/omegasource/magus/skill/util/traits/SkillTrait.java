package com.amuzil.omegasource.magus.skill.util.traits;

import net.minecraft.nbt.CompoundTag;
import org.checkerframework.checker.units.qual.C;

import javax.xml.crypto.Data;

public class SkillTrait implements DataTrait {
    private String name;

    public SkillTrait(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(getName(), name);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        name = nbt.getString(getName());
    }
}
