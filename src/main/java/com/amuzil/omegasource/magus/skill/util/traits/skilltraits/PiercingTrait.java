package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class PiercingTrait extends SkillTrait {

    private boolean piecing;

    public PiercingTrait(boolean piercing, String name) {
        super(name);
        this.piecing = piercing;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putBoolean(getName(), piecing);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        piecing = nbt.getBoolean(getName());
    }

    public void setPiecing(boolean piecing) {
        this.piecing = piecing;
        markDirty();
    }

    public boolean isPiecing() {
        return piecing;
    }
}
