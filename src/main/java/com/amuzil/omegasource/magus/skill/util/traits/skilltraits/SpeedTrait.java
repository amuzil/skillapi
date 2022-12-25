package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class SpeedTrait extends SkillTrait {

    private double speed;

    public SpeedTrait(double speed, String name) {
        super(name);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(getName(), speed);
        return super.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        speed = nbt.getDouble(getName());
    }
}
