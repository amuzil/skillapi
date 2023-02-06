package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class KeyHeldModifierListener extends ModifierListener<TickEvent> {

    private final Consumer<InputEvent.MouseButton> keyInputListener;
    private final Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int currentHolding;
    private final InputConstants.Key keyToHold;
    private boolean isHeld = false;

    public KeyHeldModifierListener(InputConstants.Key key) {
        this.modifierData = new HeldModifierData();
        this.keyToHold = key;

        this.keyInputListener = event -> {
            if (event.getButton() == key.getValue()) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    this.isHeld = true;
                    this.currentHolding = 0;
                } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                    this.isHeld = false;
                }
            }
        };

        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START) {
                if (this.isHeld) {
                    this.currentHolding++;
                }
            }
        };
    }

    @Override
    public void register(Runnable onSuccess) {
        super.register(onSuccess);
        MinecraftForge.EVENT_BUS.addListener(keyInputListener);
        MinecraftForge.EVENT_BUS.addListener(clientTickListener);
    }

    @Override
    public boolean shouldCollectModifierData(TickEvent event) {
        if(isHeld) {
            return true;
        }
        return false;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent event) {
        LogManager.getLogger().info("COLLECTING MODIFIER DATA");
        //todo check how often this is called.
        HeldModifierData data = new HeldModifierData(currentHolding);
        this.currentHolding = 0;
        return data;
    }
}
