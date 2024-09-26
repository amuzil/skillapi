package com.amuzil.omegasource.magus.skill.modifiers.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

public abstract class ModifierListener<T extends Event> implements Consumer<T> {

    Runnable onSuccess;
    protected ModifierData modifierData;

    public void register(Runnable onSuccess) {
        MinecraftForge.EVENT_BUS.addListener(this);
        this.onSuccess = onSuccess;
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public abstract void setupListener(CompoundTag compoundTag);

    public abstract boolean shouldCollectModifierData(T event);

    public abstract ModifierData collectModifierDataFromEvent(T event);

    public ModifierData getModifierData() { return modifierData; }

    @Override
    public void accept(T event) {
        if(shouldCollectModifierData(event)) {
            this.modifierData = collectModifierDataFromEvent(event);

            onSuccess.run();
        }
    }

    public abstract ModifierListener copy();
}
