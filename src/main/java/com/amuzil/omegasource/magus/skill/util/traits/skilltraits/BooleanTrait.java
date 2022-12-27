package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

/** Unlike the classes in the skilltraits package, these
 * traits exist outside the skill instance. For example
 * StanceTrait s are toggleable booleans that say whether a player has a certain stance enabled.
 */
public class BooleanTrait extends SkillTrait {

    private boolean enabled;

    public BooleanTrait(String name, boolean enabled) {
        super(name);
        this.enabled = enabled;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putBoolean(getName(), enabled);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        enabled = nbt.getBoolean(getName());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        markDirty();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void toggleEnabled() {
        setEnabled(!isEnabled());
    }
}
