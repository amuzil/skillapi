package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MousePointInput;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class KeyHoldCondition extends Condition {
    public static final int KEY_PRESS_TIMEOUT = 3;
    public List<MousePointInput> mouseInputs = new ArrayList<>();
    private Consumer<ClientTickEvent> clientTickListener;
    private int currentTotal;
    private int currentHolding;
    private final int key;
    private final int duration;
    private int timeout = -1;
    // False by default.
    private boolean release = false;
    private boolean started = false;

    public KeyHoldCondition(int key, int duration) {
        if (duration < 0)
            RadixUtil.getLogger().warn("You should not be defining a key press duration of less than 0.");

        this.currentTotal = 0;
        this.currentHolding = 0;
        this.duration = duration;
        this.key = key;
    }

    public KeyHoldCondition(int key, int duration, int timeout, boolean release) {
        if (duration < 0)
            RadixUtil.getLogger().warn("You should not be defining a key press duration of less than 0.");

        this.currentTotal = 0;
        this.currentHolding = 0;
        this.duration = duration;
        this.timeout = timeout;
        this.release = release;
        this.key = key;
        this.started = false;

        this.clientTickListener = event -> {
            if (event.phase == ClientTickEvent.Phase.START && Minecraft.getInstance().getOverlay() == null) {
                if (Magus.keyboardInputModule.keyPressed(key) || Magus.mouseInputModule.keyPressed(key)) {
                    this.started = true;
                    this.currentHolding++;
                    if (pressed(this.currentHolding, duration)) {
//                    // If the Condition doesn't require the key being released....
                        if (!release) {
                            this.onSuccess.run();
                            this.reset();
                        }
                    }
                } else {
                    if (pressed(this.currentHolding, duration)) {
                        // If the Condition requires the key being released....
                        if (release) {
                            this.onSuccess.run();
                            this.reset();
                        }
                    } else {
                        // Not held for long enough
                        if (this.currentHolding > 0) {
                            this.onFailure.run();
                            this.reset();
                        }
                    }
                }
                // If the duration is <= 3, then we want the Condition to act as a key press, rather than a hold.
//
                if (this.started) {
                    // Timeout of -1 means that this should wait forever.
                    if (timeout > -1 && this.currentTotal >= timeout) {
                        this.onFailure.run();
                    }
                    this.currentTotal++;
                }

            }
        };
        this.registerEntry();
    }

    public int getHeld() {
        return this.currentHolding;
    }

    public boolean pressed(int held, int duration) {
        if (duration <= KEY_PRESS_TIMEOUT) {
            return held > 0;
        }
        else return held >= duration;
//        LogManager.getLogger().info("Checking pressed. held:" + held + ", duration: " + duration + ", result: " + pressed);
    }

    // Should be called in either runnable by other methods, rather than manually here. Calling it manually in the class can lead
    // to race conditions and other weird bugs, none of which are ideal.
    public void reset() {
        this.currentTotal = 0;
        this.currentHolding = 0;
        this.started = false;
    }

    public int getKey() {
        return this.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof KeyHoldCondition other)) {
            return false;
        } else {
//            System.out.println("this: stored in tree -> " + this);
//            System.out.println("other: activeCondition from user input -> " + other);
            return Objects.equals(key, other.getKey()) &&
                    other.currentHolding >= duration;
        }
    }

    @Override
    public void register(String name, Runnable onSuccess, Runnable onFailure) {
        super.register(name, onSuccess, onFailure);
    }

    @Override
    public void register() {
        super.register();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class,
                clientTickListener);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
    }

    @Override
    public String toString() {
        return String.format("%s[ %s, key=%s, held=%d, d=%d, t=%d, r=%b ]", this.getClass().getSimpleName(), name(),
                key, currentHolding, duration, timeout, release);
    }
}
