package com.amuzil.omegasource.skillapi.data.conditions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Function;

public class ForgeEventCondition<E extends Event> extends EventCondition<E> {

    public ForgeEventCondition() {
        this.condition = null;
    }

    public ForgeEventCondition(Function<E, Boolean> condition) {
        this.condition = condition;
    }

    @Override
    public void register(Runnable success, Runnable expire) {
        super.register(success, expire);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
