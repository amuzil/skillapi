package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.mouse;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;
import java.util.function.Function;

public class MouseScrollCondition extends EventCondition<InputEvent.MouseScrollingEvent> {

    private final int timeout;
    private final int duration;
    private int currentScrolling;
    private float currentScrollTotal;
    private float currentScrollDelta;
    private Consumer<TickEvent.ClientTickEvent> clientTicker;
    public MouseScrollCondition(Class<InputEvent.MouseScrollingEvent> eventType, Function<InputEvent.MouseScrollingEvent, Boolean> condition) {
        super(eventType, condition);
        this.clientTicker = event -> {

        };
    }

    @Override
    public void register() {
        super.register();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, clientTicker);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(clientTicker);
    }
}
