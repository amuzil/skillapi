package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MousePointInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseMotionInput;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyHoldCondition extends Condition {
    //TODO: Make this configurable
    public static final int KEY_PRESS_TIMEOUT = 3;
    private final Consumer<ClientTickEvent> clientTickListener;
    private int currentTotal;
    private int currentHolding;
    private final int key;
    // False by default.
    private final boolean release;
    private boolean started = false;
    public List<MousePointInput> mouseInputs = new ArrayList<>();

    public KeyHoldCondition(int key, int duration, int timeout, boolean release) {
        if (duration < 0)
            RadixUtil.getLogger().warn("You should not be defining a key press duration of less than 0.");

        this.currentTotal = 0;
        this.currentHolding = 0;
        this.release = release;
        this.key = key;
        this.started = false;

        this.clientTickListener = event -> {
            if (event.phase == ClientTickEvent.Phase.START && Minecraft.getInstance().getOverlay() == null) {
                if (Magus.keyboardInputModule.keyPressed(key) || Magus.mouseInputModule.keyPressed(key)) {
                    this.started = true;
                    this.currentHolding++;
                    Minecraft mci = Minecraft.getInstance();
                } else {
                    if (pressed(this.currentHolding, duration)) {
                        // If the Condition requires the key being released....
                        if (release) {
                            this.onSuccess.run();
                        }
                    } else {
                        // Not held for long enough
                        if (this.currentHolding > 0) {
                            this.onFailure.run();
                        }
                    }
                }
                // If the duration is <= 3, then we want the Condition to act as a key press, rather than a hold.
//                if (pressed(this.currentHolding, duration)) {
//                    // If the Condition doesn't require the key being released....
//                    if (!release) {
//                        this.onSuccess.run();
//                    }
//                }
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

    public boolean pressed(int held, int duration) {
        boolean pressed = held >= duration || held > 0 && duration <= KEY_PRESS_TIMEOUT;
//        LogManager.getLogger().info("Checking pressed. held:" + held + ", duration: " + duration + ", result: " + pressed);
        return pressed;
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
    public String name() {
        return "key_hold";
    }
}
