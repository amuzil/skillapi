package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

/**
 * Basic DamageTrait class for skills. Lets the user determine
 * what kind of damage it's for. E.g a Lightning Arc technique might take
 * multiple damage traits for the shockwave, chain hits, e.t.c.
 */
public class DamageTrait extends SkillTrait {

    private double damage;

    public DamageTrait(double damage, String name) {
        super(name);
        this.damage = damage;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(getName(), damage);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        damage = nbt.getDouble(getName());
    }

    public void setDamage(double damage) {
        this.damage = damage;
        markDirty();
    }

    public double getDamage() {
        return this.damage;
    }

    @Override
    public void reset() {
        super.reset();
        setDamage(0);
    }
}
