package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TimedEventCondition<E extends Event> extends EventCondition<E> {

    Condition subCondition;
    int timeout;
    int current;

    public TimedEventCondition() {
        this.subCondition = new SimpleCondition();
        this.timeout = -1;
        this.current = 0;
        this.onSuccess = null;
        this.onExpire = null;
    }

    public TimedEventCondition(Condition subCondition, int timeout, int current, Runnable onSuccess, Runnable onExpire) {
        this.subCondition = subCondition;
        this.timeout = timeout;
        this.current = current;
        this.onSuccess = onSuccess;
        this.onExpire = onExpire;
    }

    @Override
    public void register(Runnable onSuccess, Runnable onExpire) {
        super.register(onSuccess, onExpire);
        subCondition.register(onSuccess, onExpire);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(this);
        subCondition.unregister();
    }

    // Simplified
    @SubscribeEvent
    @Override
    public void listen(E event) {
        current += 1;
        if (current >= timeout) {
            onExpire.run();
        }
    }
}
