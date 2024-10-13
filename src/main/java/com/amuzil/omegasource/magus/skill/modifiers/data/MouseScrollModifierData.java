package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.MouseScrollModifierListener;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;

public class MouseScrollModifierData extends BaseModifierData {
    private float totalScrollDelta;


    public MouseScrollModifierData(float totalScrollDelta) {
        this.totalScrollDelta = totalScrollDelta;
    }

    public MouseScrollModifierData() {
        this(0);
    }

    @Override
    protected void mergeFields(ModifierData modifierData) {
        MouseScrollModifierData scrollData = (MouseScrollModifierData) modifierData;
        this.totalScrollDelta += scrollData.totalScrollDelta;
    }

    @Override
    public void print() {
        LogManager.getLogger().info("Total Mouse Scroll Delta: " + totalScrollDelta);
    }

    @Override
    public void reset() {
        this.totalScrollDelta = 0;
    }

    @Override
    public ModifierData copy() {
        return new MouseScrollModifierData();
    }

    @Override
    public String getName() {
        return "MouseScrollModifier";
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.totalScrollDelta = nbt.getFloat("total_scroll_delta");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putFloat("total_scroll_delta",  totalScrollDelta);
        return tag;
    }
}
