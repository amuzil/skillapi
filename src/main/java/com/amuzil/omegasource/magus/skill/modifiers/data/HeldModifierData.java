package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;


public class HeldModifierData extends BaseModifierData {

    private int duration;
    private boolean currentlyHeld;
    private String formName;

    public HeldModifierData() {
        this.duration = 0;
        this.currentlyHeld = true;
    }

    public HeldModifierData(int duration, boolean currentlyHeld) {
        super();
        this.duration = duration;
        this.currentlyHeld = currentlyHeld;
        this.formName = "";
    }

    public HeldModifierData(int duration, boolean currentlyHeld, String formName) {
        super();
        this.duration = duration;
        this.currentlyHeld = currentlyHeld;
        this.formName = formName;
    }

    public void setFormName(String name) {
        this.formName = name;
    }

    public boolean held() {
        return this.currentlyHeld;
    }

    public String getFormName() {
        return this.formName;
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
        compoundTag.putString("formName", formName);

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
        this.formName = compoundTag.getString("formName");
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        HeldModifierData heldModifierData = (HeldModifierData) modifierData;
        // Checks to make sure forms are consistent
        if (this.currentlyHeld && heldModifierData.getFormName().equals(getFormName())) {
            this.duration = this.duration + heldModifierData.duration;
            this.currentlyHeld = heldModifierData.currentlyHeld;
        }
    }

    @Override
    public void reset() {
        this.duration = 0;
        this.currentlyHeld = true;
        this.formName = "";
    }

    @Override
    public void print() {
//        LogManager.getLogger().info("HeldModifierData duration: " + duration);
//        LogManager.getLogger().info("HeldModifierData currentlyHeld: " + currentlyHeld);
//        LogManager.getLogger().info("HeldModifierData formName: " + formName);
    }
}
