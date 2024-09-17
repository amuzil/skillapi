package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Consumer;

public class MouseInputModule extends InputModule {

    private final Consumer<TickEvent> tickEventConsumer;
    private List<Integer> glfwKeysDown;
    private static final Map<String, Integer> movementKeys = new HashMap<>();

    //How scroll delta works: every physical "tick" forwards on the mouse is 1.0, and backwards
    // is -1.0. Therefore, you'd need a tracker over time, like a key held event, for the mouse wheel.
    // Except you're not pressing it, you're spinning it....
    private double mouseScrollDelta;
    private final Consumer<InputEvent.MouseButton> mouseListener;
    private final Consumer<InputEvent.MouseScrollingEvent> mouseScrollListener;
    private Form activeForm = new Form();
    private int ticksSinceActivated = 0;
    private int ticksSinceModifiersSent = 0;

    //todo make these thresholds configurable and make them longer. Especially the timeout threshold.
    private final int tickActivationThreshold = 15;
    private final int tickTimeoutThreshold = 60;
    private final int modifierTickThreshold = 10;
    private boolean listen;
    Minecraft mc = Minecraft.getInstance();

    // TODO: Fix this such that any tree requiring a form relies on the input
    // module activating a form rather than relying on the raw input data for those forms.
    // This way, the trees for different complex methods (such as VR and multikey)
    // remain functionally the same, they just check different input modules for whether the same
    // forms are activated.
    public MouseInputModule() {
        this.glfwKeysDown = new ArrayList<>();
        this.listen = true;

        this.mouseListener = mouseEvent -> {
            int keyPressed = mouseEvent.getButton();
                switch (mouseEvent.getAction()) {
                    case InputConstants.PRESS -> {
                        if (!glfwKeysDown.contains(keyPressed))
                            glfwKeysDown.add(keyPressed);
                    }
                    case InputConstants.REPEAT -> {
                        if (!glfwKeysDown.contains(keyPressed)) {
                            glfwKeysDown.add(keyPressed);
                        }
                    }
                    case InputConstants.RELEASE -> {
                        if (glfwKeysDown.contains(keyPressed)) {
                            glfwKeysDown.remove((Integer) keyPressed);
                        }
                    }
            //    }
            }
        };

        this.mouseScrollListener = mouseScrollingEvent -> {
          this.mouseScrollDelta = mouseScrollingEvent.getScrollDelta();
        };

        this.tickEventConsumer = tickEvent -> {

            ticksSinceModifiersSent++;
            if (ticksSinceModifiersSent > modifierTickThreshold && !modifierQueue.isEmpty()) {
                sendModifierData();
            }

            //cleanMCKeys();

            if(activeForm.name() != null) {
                ticksSinceActivated++;
                if(ticksSinceActivated >= tickActivationThreshold) {
                    if (lastActivatedForm != null)
                        LogManager.getLogger().info("LAST FORM ACTIVATED: " + lastActivatedForm.name() + " | FORM ACTIVATED: " + activeForm.name());
                    else
                        LogManager.getLogger().info("FORM ACTIVATED: " + activeForm.name());
//                    MagusNetwork.sendToServer(new ConditionActivatedPacket(activeForm));
                    lastActivatedForm = activeForm;
                    activeForm = new Form();
                    ticksSinceActivated = 0;
                }
            }
            else {
                ticksSinceActivated++;
                if (ticksSinceActivated >= tickTimeoutThreshold) {
                    lastActivatedForm = null;
                    ticksSinceActivated = 0;
                }
            }
        };
    }

    private void sendModifierData() {
        LogManager.getLogger().info("SENDING MODIFIER DATA");
        synchronized (modifierQueue) {
            MagusNetwork.sendToServer(new SendModifierDataPacket(modifierQueue.values().stream().toList()));
            ticksSinceModifiersSent = 0;
            modifierQueue.clear();
        }
    }

    public void resetKeys() {
        glfwKeysDown = new ArrayList<>();
    }

    public void cleanMCKeys() {
        // Fixes some weird mouse and other key issues.
        for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if (!key.isDown()) {
                if (glfwKeysDown.contains(key.getKey().getValue()))
                    glfwKeysDown.remove((Integer) key.getKey().getValue());
            }
        }
    }

    @Override
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute, Condition condition) {
        //generate condition from InputData.
        Runnable onSuccess = () -> {
            if(mc.level != null) {
                //this section is to prevent re-activating
                // single condition forms when you hold the activation key for Held modifiers

                //TODO: Fix an issue where it doesn't let players re-activate forms outside of the held modifier.
                // I.e account for modifiers here.
                if(formToExecute != lastActivatedForm) {
                    //LogManager.getLogger().info("FORM ACTIVATED: " + formToExecute.name());
                    activeForm = formToExecute;
                }

                ticksSinceActivated = 0;
            }
            //Reset condition
        };
        Runnable onFailure = () -> {
            activeForm = new Form();
            //reset conditions?
        };
        Condition formCondition = ConditionBuilder.instance()
                .fromInputData(formExecutionInputs)
                .build();
        if(formCondition != null) {
            //Register listeners for condition created.
            formCondition.register(formToExecute.name(), onSuccess, onFailure);
            //add condition to InputModule registry so that it can be tracked.
            formInputs.put(formCondition, formToExecute);
        } else {
            //todo errors/logging
        }

    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseButton.class, mouseListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseScrollingEvent.class, mouseScrollListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.class, tickEventConsumer);
    }

    @Override
    public void unregisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(mouseListener);
        MinecraftForge.EVENT_BUS.unregister(mouseScrollListener);
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        formInputs.forEach((condition, form) -> condition.unregister());
    }

    public void registerInputs() {
        formInputs.forEach((condition, form) -> {
            condition.register();
        });
    }

    @Override
    public void toggleListeners() {
        if (!listen) {
            registerListeners();
            registerInputs();
            listen = true;
            System.out.println("Enabled!");
        } else {
            unregisterInputs();
            listen = false;
            System.out.println("Disabled!");
        }
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.contains(key);
    }

    public static void determineMotionKeys() {
        Arrays.stream(Minecraft.getInstance().options.keyMappings).toList().forEach(keyMapping -> {
            if(keyMapping.getCategory().equals(KeyMapping.CATEGORY_MOVEMENT)) {
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
