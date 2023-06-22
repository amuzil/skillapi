package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Consumer;

public class KeyPressCondition extends Condition {
    private final Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int current;
    private final int key;

    public KeyPressCondition(int key, int timeout) {
        this.key = key;

        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START && event.side == LogicalSide.CLIENT) {
                if(((KeyboardMouseInputModule) Magus.keyboardInputModule).keyPressed(getKey()))  {
                    //What key is 0 hello???
                    System.out.println(getKey());
                    this.onSuccess.run();
                } else if(current >= timeout) {
                    this.onFailure.run();
                } else {
                    current++;
                }
            }
        };
    }

    public int getKey() {
        return key;
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        super.register(onSuccess, onFailure);
        RadixUtil.getLogger().debug("Current Key upon registration: " + getKey());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class,
                clientTickListener);
    }

    @Override
    public void unregister() {
        super.unregister();
        RadixUtil.getLogger().debug("Current Key upon registration: " + getKey());
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
    }
}
