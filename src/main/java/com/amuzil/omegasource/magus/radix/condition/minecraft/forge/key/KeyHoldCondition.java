package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class KeyHoldCondition extends Condition {
    private final Consumer<ClientTickEvent> clientTickListener;
    private int currentTotal;
    private int currentHolding;

    public KeyHoldCondition(int key, int duration, int timeout) {
        RadixUtil.assertTrue(duration >= 0, "duration must be >= 0");
        RadixUtil.assertTrue(timeout >= 0, "timeout must be >= 0");

        this.currentTotal = 0;
        this.currentHolding = 0;

        this.clientTickListener = event -> {
            if (event.phase == ClientTickEvent.Phase.START) {
                if (((KeyboardMouseInputModule) Magus.inputModule).keyPressed(key)) {
                    this.currentHolding++;
                }
                if (this.currentHolding >= duration) {
                    this.onSuccess.run();
                }

                if (this.currentTotal >= timeout) {
                    this.onFailure.run();
                }
                this.currentTotal++;
            }
        };
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        super.register(onSuccess, onFailure);
        MinecraftForge.EVENT_BUS.addListener(clientTickListener);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
    }
}
