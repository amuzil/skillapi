package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;

public class MultiModifierData extends BaseModifierData {

    private int count;
    private String formName;

    public MultiModifierData() {
        this(1, "");
    }

    public MultiModifierData(int count) {
        this(count, "");
    }

    public MultiModifierData(int count, String formName) {
        super();
        this.count = count;
        this.formName = formName;
    }

    public String getFormName() {
        return this.formName;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public String getName() {
        return "MultiModifier";
    }

    @Override
    public boolean serversideOnly() {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();

        compoundTag.putInt("count", count);
        compoundTag.putString("formName", formName);

        return compoundTag;
    }

    @Override
    public MultiModifierData copy() {
        return new MultiModifierData();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.count = compoundTag.getInt("count");
        this.formName = compoundTag.getString("formName");
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        MultiModifierData heldModifierData = (MultiModifierData) modifierData;
        this.count = this.count + heldModifierData.count;
    }

    @Override
    public void reset() {
        this.count = 1;
        this.formName = "";
    }

    @Override
    public void print() {
        LogManager.getLogger().info("MultiModifierData count: " + count);
    }
}
