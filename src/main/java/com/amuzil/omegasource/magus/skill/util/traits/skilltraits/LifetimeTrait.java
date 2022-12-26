package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

/**
 * Lifetime of an entity or projectile, in ticks.
 */
public class LifetimeTrait extends SkillTrait {
    private int lifetime;

    public LifetimeTrait(int lifetime, String name) {
        super(name);
        this.lifetime = lifetime;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(getName(), lifetime);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        lifetime = nbt.getInt(getName());
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
        markDirty();
    }

    public int getLifetime() {
        return lifetime;
    }
}
