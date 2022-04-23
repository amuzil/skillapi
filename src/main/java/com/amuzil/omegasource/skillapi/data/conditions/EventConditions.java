package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class EventConditions {

    public abstract class EventCondition<E extends Event> implements Condition {
        Runnable success;
        Runnable expire;

        abstract void listen(E event);
    }

    public abstract class ForgeEventCondition<E extends Event> extends EventCondition<E> {

        @Override
        public void register(Runnable success, Runnable expire) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
}