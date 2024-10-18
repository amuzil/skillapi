package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;

public class MultiModifierData extends BaseModifierData {

    private int count;

    public MultiModifierData() {
        this.count = 1;
    }

    public MultiModifierData(int count) {
        super();
        this.count = count;
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

        return compoundTag;
    }

    @Override
    public MultiModifierData copy() {
        return new MultiModifierData();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.count = compoundTag.getInt("count");
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
    }

    @Override
    public void print() {
        LogManager.getLogger().info("MultiModifierData count: " + count);
    }
}
