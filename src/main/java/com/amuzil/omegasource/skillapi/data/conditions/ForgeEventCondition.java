package com.amuzil.omegasource.skillapi.data.conditions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public abstract class ForgeEventCondition<E extends Event> extends EventCondition<E> {

    @Override
    public void register(Runnable success, Runnable expire) {
        super.register(success, expire);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
