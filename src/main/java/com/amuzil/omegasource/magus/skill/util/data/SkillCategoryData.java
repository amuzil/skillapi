package com.amuzil.omegasource.magus.skill.util.data;

import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;

public class SkillCategoryData implements DataTrait {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public void markClean() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
