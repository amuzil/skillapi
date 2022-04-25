package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public abstract class EventCondition<E extends Event> implements Condition {
        Runnable success;
        Runnable expire;

        abstract void listen(E event);

        @Override
        public void register(Runnable success, Runnable expire) {
            this.success = success;
            this.expire = expire;
        }
    }
