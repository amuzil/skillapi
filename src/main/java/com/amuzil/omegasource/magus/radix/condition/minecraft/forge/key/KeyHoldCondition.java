package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public class KeyHoldCondition extends Condition {
    private final Consumer<ClientTickEvent> clientTickListener;
    private int currentTotal;
    private int currentHolding;
    private int key;

    // False by default.
    private boolean release;

    //TODO: Make this configurable
    public static final int KEY_PRESS_TIMEOUT = 3;

    public KeyHoldCondition(int key, int duration, int timeout, boolean release) {
        RadixUtil.assertTrue(duration >= 1, "duration must be >= 0");
        RadixUtil.assertTrue(timeout >= 1, "timeout must be >= 0");

        this.currentTotal = 0;
        this.currentHolding = 0;
        this.release = release;
        this.key = key;

        this.clientTickListener = event -> {
            if (event.phase == ClientTickEvent.Phase.START) {
                if (((KeyboardMouseInputModule) Magus.keyboardInputModule).keyPressed(key)) {
                    this.currentHolding++;
                } else {
                    if (pressed(this.currentHolding, duration)) {
                        // If the Condition requires the key being released....
                        if (release)
                            this.onSuccess.run();
                    }
                }
                // If the duration is <= 3, then we want the Condition to act as a key press, rather than a hold.
                if (pressed(this.currentHolding, duration)) {
                    // If the Condition doesn't require the key being released....
                    if (!release)
                        this.onSuccess.run();
                }

                if (this.currentTotal >= timeout) {
                    this.onFailure.run();
                }
                this.currentTotal++;
            }
        };
    }

    public boolean pressed(int held, int duration) {
        return held >= duration || held > 0 && duration <= KEY_PRESS_TIMEOUT;
    }

    public int getKey() {
        return this.key;
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        super.register(onSuccess, onFailure);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class,
                clientTickListener);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
    }
}
