package com.amuzil.omegasource.skillapi.data.conditions.key;

import com.amuzil.omegasource.skillapi.activateable.KeyInfo;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

public abstract class KeyPressCondition extends KeyEventCondition<InputEvent.KeyInputEvent> {

    public KeyPressCondition(KeyInfo key) {
        super(key);
    }

    @Override
    public void listen(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == getKeyInfo().getKey().getValue()) {
            success.run();
        }
    }
}
