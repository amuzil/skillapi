package com.amuzil.omegasource.magus.radix.condition.minecraft.forge;

import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;
import java.util.function.Function;

public class TimedEventCondition<E extends Event> extends Condition {

    private final Consumer<E> listener;
    private final Consumer<TickEvent.ClientTickEvent> timer;
    private final Class<E> eventType;
    private int time;

    public TimedEventCondition(Class<E> eventType, Function<E, Boolean> condition,
                               int timeout) {
        this.eventType = eventType;
        this.listener = event -> {
            if (condition.apply(event)) {
                this.onSuccess.run();
                reset();
            } else {
                this.onFailure.run();
                reset();
            }
        };
        this.time = 0;
        this.timer = event -> {
            time++;
            if (time > timeout) {
                this.onFailure.run();
                reset();
            }
        };
    }

    public void reset() {
        this.time = 0;
    }
    @Override
    public void register() {
        super.register();
        //This is required because a class type check isn't inbuilt, for some reason.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false,
                eventType, listener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false,
                TickEvent.ClientTickEvent.class, timer);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(listener);
        MinecraftForge.EVENT_BUS.unregister(timer);
    }

    @Override
    public String name() {
        return "event_timed_trigger";
    }
}
