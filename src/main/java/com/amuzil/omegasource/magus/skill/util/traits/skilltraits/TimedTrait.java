package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

/**
 * The most generic trait of all time. Works for:
 * -Fire time, charging, lifetime, potion duration, e.t.c.
 */
public class TimedTrait extends SkillTrait {
    private int time;

    public TimedTrait(int time, String name) {
        super(name);
        this.time = time;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(getName(), time);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        time = nbt.getInt(getName());
    }

    public void setTime(int time) {
        this.time = time;
        markDirty();
    }

    public int getTime() {
        return time;
    }
}
