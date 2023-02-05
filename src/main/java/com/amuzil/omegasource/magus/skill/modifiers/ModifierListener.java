package com.amuzil.omegasource.magus.skill.modifiers;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

public abstract class ModifierListener<T extends Event> implements Consumer<T> {

    Runnable onSuccess;
    private ModifierData modifierData;

    public void register(Runnable onSuccess) {
        MinecraftForge.EVENT_BUS.addListener(this);
        this.onSuccess = onSuccess;
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public abstract ModifierData collectModifierDataFromEvent(T event);

    public ModifierData getModifierData() { return modifierData; }

    @Override
    public void accept(T event) {
        this.modifierData.add(collectModifierDataFromEvent(event));

        onSuccess.run();
    }
}
