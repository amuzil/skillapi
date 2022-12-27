package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

public class StringTrait extends SkillTrait {

    private String info;

    public StringTrait(String name, String info) {
        super(name);
        this.info = info;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putString(getName(), info);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        info = nbt.getString(getName());
    }

    public void setInfo(String info) {
        this.info = info;
        markDirty();
    }

    public String getInfo() {
        return info;
    }
}
