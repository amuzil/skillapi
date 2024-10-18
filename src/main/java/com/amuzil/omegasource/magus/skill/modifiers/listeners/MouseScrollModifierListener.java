package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
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
        // Half a second, or 20 ticks
//        Magus.LOGGER.warn(System.currentTimeMillis());
        return inputModule.scrollTimeout % 3 == 0 &&
                inputModule.getMouseScrollDelta() != 0 ||
                inputModule.resetScrolling;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent.ClientTickEvent event) {
        if (inputModule.resetScrolling) {
            this.totalScrollDelta = 0;
            inputModule.resetScrolling = false;
        }
        else this.totalScrollDelta += inputModule.getMouseScrollDelta();
        return new MouseScrollModifierData(totalScrollDelta);
    }

    @Override
    public ModifierListener copy() {
        return new MouseScrollModifierListener();
    }
}
