package com.amuzil.omegasource.skillapi.data.conditions.key;

import com.amuzil.omegasource.skillapi.activateable.KeyInfo;
import com.amuzil.omegasource.skillapi.data.conditions.TimedEventCondition;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

public abstract class KeyHoldCondition<E extends InputEvent.KeyInputEvent> extends TimedEventCondition<E> implements KeyCondition {
    int duration;
    private KeyInfo key;

    public KeyHoldCondition(KeyInfo key) {
        this.key = key;
    }

    @Override
    public void listen(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == getKeyInfo().getKey().getValue()) {
            //We don't actually want success to run here, we want it to run if this condition is satisfied
            //and the key has been held for a certain amount of time
            success.run();
        }
    }

    @Override
    public KeyInfo getKeyInfo() {
        return key;
    }
}
