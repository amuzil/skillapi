package com.amuzil.omegasource.skillapi.data.conditions;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public abstract class TimedEventCondition<E extends Event> extends EventCondition<E> {

    int duration;
    int maxDuration;

    @Override
    public void register(Runnable success, Runnable expire) {
        super.register(success, expire);
    }

    @Override
    public void listen(E event) {
        duration++;
        if (duration >= maxDuration)
            success.run();
        else expire.run();
    }
}
