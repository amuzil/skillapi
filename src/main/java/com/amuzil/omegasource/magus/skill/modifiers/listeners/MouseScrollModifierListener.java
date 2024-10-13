package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.MouseScrollModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.TickEvent;

public class MouseScrollModifierListener extends ModifierListener<TickEvent.ClientTickEvent> {
    private float totalScrollDelta = 0;
    KeyboardMouseInputModule inputModule = (KeyboardMouseInputModule) Magus.keyboardMouseInputModule;

    @Override
    public void setupListener(CompoundTag compoundTag) {

    }

    @Override
    public boolean shouldCollectModifierData(TickEvent.ClientTickEvent event) {
        RadixUtil.getLogger().info("Mouse Scroll Listener:" + inputModule.getMouseScrollDelta());
        return inputModule.getMouseScrollDelta() != 0;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent.ClientTickEvent event) {
        this.totalScrollDelta += inputModule.getMouseScrollDelta();
        MouseScrollModifierData data = new MouseScrollModifierData(totalScrollDelta);
        return data;
    }

    @Override
    public ModifierListener copy() {
        return new MouseScrollModifierListener();
    }
}
