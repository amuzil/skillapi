package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;

public class HeldModifierData extends BaseModifierData {

    private int duration;

    public HeldModifierData() {
        this.duration = 0;
    }

    public HeldModifierData(int duration) {
        super();
        this.duration = duration;
    }

    @Override
    public String getName() {
        return "HeldModifier";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();

        compoundTag.putInt("duration", duration);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.duration = compoundTag.getInt("duration");
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        HeldModifierData heldModifierData = (HeldModifierData) modifierData;
        this.duration += heldModifierData.duration;
    }

    @Override
    public void print() {
        LogManager.getLogger().info("HeldModifierData duration: " + duration);
    }
}
