package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.mouse;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class MouseScrollCondition extends Condition {

    private int timeout;
    private int timer;
    private int duration;
    protected int currentScrolling;
    private float currentScrollTotal;
    private double maxScrollTotal;
    private double currentScrollDelta;
    private MouseDataBuilder.Direction direction;
    private Consumer<TickEvent.ClientTickEvent> clientTicker;

    public MouseScrollCondition(MouseDataBuilder.Direction direction, int duration, float total, int timeout) {
        this.direction = direction;
        this.duration = duration;
        this.maxScrollTotal = total;
        this.timeout = timeout;
        this.timer = 0;

        this.clientTicker = tickEvent -> {

            currentScrollDelta = ((KeyboardMouseInputModule) Magus.keyboardMouseInputModule).getMouseScrollDelta();
            currentScrollTotal += ((KeyboardMouseInputModule) Magus.keyboardMouseInputModule).getMouseScrollDelta();


            // Any instant of the required direction being fulfilled
            if (duration < 0) {
                if (total != 0) {
                    // If the current total is equal to the max total...
                    if (currentScrollTotal == maxScrollTotal) {
                        this.onSuccess.run();
                    }
                }
                else {
                    if (this.direction.getDirection() == (int) currentScrollDelta) {
                        this.onSuccess.run();
                    }
                }
            }

            // Otherwise, we need to track over the duration
            else {
                if (currentScrollDelta == direction.getDirection()) {
                    currentScrolling++;
                }
                if (currentScrolling >= duration) {
                    this.onSuccess.run();
                }
            }


            // Time out
            if (timer > timeout && timeout > -1) {
                this.onFailure.run();
            }

            // Fail because wrong direction. Works when we need a specific direction for a certain amount of ticks.
            if (duration > -1) {
                if (maxScrollTotal == 0) {
                    if (currentScrollDelta != direction.getDirection()) {
                        this.onFailure.run();
                    }
                }
            }

            timer++;
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

    @Override
    public int hashCode() {
        return Objects.hash(direction.getDirection(), duration, maxScrollTotal, timeout);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof MouseScrollCondition other)) {
            return false;
        } else {
//            System.out.println("this: stored in tree -> " + this);
//            System.out.println("other: activeCondition from user input -> " + other);
            return Objects.equals(direction.getDirection(), other.direction.getDirection()) &&
                    /* Makes sure an alternative key condition that's been pressed has been pressed at least as long
                     * as the currently compared condition. */
                    other.currentScrolling >= duration && other.maxScrollTotal == maxScrollTotal && timeout == other.timeout;
        }
    }

}
