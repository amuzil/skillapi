package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class SizeTrait extends SkillTrait {

    private double size;

    public SizeTrait(float size, String name) {
        super(name);
        this.size = size;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("size", size);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        size = nbt.getDouble("size");
    }
}
