package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.FormActivatedPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.*;
import java.util.function.Consumer;

import com.amuzil.omegasource.magus.skill.util.data.KeyboardData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;

public class KeyboardMouseInputModule extends InputModule {

    private final Consumer<TickEvent> tickEventConsumer;
    private final List<Integer> glfwKeysDown;
    private static final Map<String, Integer> movementKeys = new HashMap<>();
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<InputEvent.MouseButton> mouseListener;

    private Form activeForm = null;
    private int ticksSinceActivated = 0;
    private int ticksSinceModifiersSent = 0;

    //todo make these thresholds configurable
    private final int tickActivationThreshold = 4;
    private final int modifierTickThreshold = 10;
    Minecraft mc = Minecraft.getInstance();

    public KeyboardMouseInputModule() {
        this.glfwKeysDown = new ArrayList<>();

        this.keyboardListener = keyboardEvent -> {
            int keyPressed = keyboardEvent.getKey();
            switch (keyboardEvent.getAction()) {
                case InputConstants.PRESS -> {
                    glfwKeysDown.add(keyPressed);
                }
                case InputConstants.REPEAT -> {
                    if (!glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.add(keyPressed);
                    }
                }
                case InputConstants.RELEASE -> {
                    if (glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.remove(glfwKeysDown.indexOf(keyPressed));
                    }
                }
            }
        };

        this.mouseListener = mouseEvent -> {
            int keyPressed = mouseEvent.getButton();
            if(!KeyboardData.ignore(keyPressed)) {
                switch (mouseEvent.getAction()) {
                    case InputConstants.PRESS -> {
                        glfwKeysDown.add(keyPressed);
                    }
                    case InputConstants.REPEAT -> {
                        if (!glfwKeysDown.contains(keyPressed)) {
                            glfwKeysDown.add(keyPressed);
                        }
                    }
                    case InputConstants.RELEASE -> {
                        if (glfwKeysDown.contains(keyPressed)) {
                            glfwKeysDown.remove(glfwKeysDown.indexOf(keyPressed));
                        }
                    }
                }
            }
        };

        tickEventConsumer = tickEvent -> {
            ticksSinceModifiersSent++;
            if(ticksSinceModifiersSent > modifierTickThreshold && !modifierQueue.isEmpty()) {
                sendModifierData();
            }
            if(activeForm != null) {
                ticksSinceActivated++;
                if(ticksSinceActivated >= tickActivationThreshold) {
                    LogManager.getLogger().info("FORM ACTIVATED :" + activeForm.name());
                    MagusNetwork.sendToServer(new FormActivatedPacket(activeForm));
                    lastActivatedForm = activeForm;
                    activeForm = null;
                    ticksSinceActivated = 0;
                }
            }
        };
        MinecraftForge.EVENT_BUS.addListener(keyboardListener);
        MinecraftForge.EVENT_BUS.addListener(mouseListener);
        MinecraftForge.EVENT_BUS.addListener(tickEventConsumer);
    }

    private void sendModifierData() {
        LogManager.getLogger().info("SENDING MODIFIER DATA");
        synchronized (modifierQueue) {
            MagusNetwork.sendToServer(new SendModifierDataPacket(modifierQueue.values().stream().toList()));
            ticksSinceModifiersSent = 0;
            modifierQueue.clear();
        }
    }

    @Override
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute) {
        //generate condition from InputData.
        Runnable onSuccess = () -> {
            if(mc.level != null) {
                //this section is to prevent re-activating
                // single condition forms when you hold the activation key for Held modifiers
                if(formToExecute != lastActivatedForm) {
                    activeForm = formToExecute;
                }
                ticksSinceActivated = 0;
            }
            //reset condition?
        };
        Runnable onFailure = () -> {
            activeForm = null;
            //reset conditions?
           // Magus.radixTree.burn();
        };
        Condition formCondition = new ConditionBuilder()
                .fromInputData(formExecutionInputs)
                .build();
        if(formCondition != null) {
            //Register listeners for condition created.
            formCondition.register(onSuccess, onFailure);
            //add condition to InputModule registry so that it can be tracked.
            _formInputs.put(formCondition, formToExecute);
        } else {
            //todo errors/logging
        }

    }

    @Override
    public void unregisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(keyboardListener);
        MinecraftForge.EVENT_BUS.unregister(mouseListener);
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        _formInputs.forEach((condition, form) -> condition.unregister());
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.contains(key);
    }

    public static void determineMotionKeys() {
        Arrays.stream(Minecraft.getInstance().options.keyMappings).toList().forEach(keyMapping -> {
            if(keyMapping.getCategory().equals(KeyMapping.CATEGORY_MOVEMENT)) {
                LogManager.getLogger().info(keyMapping.getName());
                movementKeys.put(keyMapping.getName(), keyMapping.getKey().getValue());
            }
        });
    }

    public static Map<String, Integer> getMovementKeys() {
        return movementKeys;
    }

    public boolean isDirectionKey(int key) {
        return movementKeys.containsValue(Integer.valueOf(key));
    }
}
