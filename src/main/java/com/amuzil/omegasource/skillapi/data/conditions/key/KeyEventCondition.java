package com.amuzil.omegasource.skillapi.data.conditions.key;

import com.amuzil.omegasource.skillapi.activateable.KeyInfo;
import com.amuzil.omegasource.skillapi.data.conditions.EventCondition;
import net.minecraftforge.client.event.InputEvent;

public abstract class KeyEventCondition<E extends InputEvent.KeyInputEvent> extends EventCondition<E> implements KeyCondition {

    private KeyInfo key;

    public KeyEventCondition(KeyInfo key) {
        this.key = key;
    }

    @Override
    public KeyInfo getKeyInfo() {
        return key;
    }

}
