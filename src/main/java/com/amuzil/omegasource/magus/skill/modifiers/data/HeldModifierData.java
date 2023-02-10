package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;

public class HeldModifierData extends BaseModifierData {

    private int duration;
    private boolean currentlyHeld;

    public HeldModifierData() {
        this.duration = 0;
    }

    public HeldModifierData(int duration, boolean currentlyHeld) {
        super();
        this.duration = duration;
        this.currentlyHeld = currentlyHeld;
    }

    @Override
    public String getName() {
        return "HeldModifier";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();

        compoundTag.putInt("duration", duration);
        compoundTag.putBoolean("currentlyHeld", currentlyHeld);

        return compoundTag;
    }

    @Override
    public HeldModifierData copy() {
        return new HeldModifierData();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.duration = compoundTag.getInt("duration");
        this.currentlyHeld = compoundTag.getBoolean("currentlyHeld");
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        HeldModifierData heldModifierData = (HeldModifierData) modifierData;
        if(!this.currentlyHeld) {
            this.duration = this.duration + heldModifierData.duration;
            this.currentlyHeld = heldModifierData.currentlyHeld;
        }
    }

    @Override
    public void reset() {
        this.duration = 0;
        this.currentlyHeld = true;
    }

    @Override
    public void print() {
        LogManager.getLogger().info("HeldModifierData duration: " + duration);
        LogManager.getLogger().info("HeldModifierData currentlyHeld: " + currentlyHeld);
    }
}
