package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
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


public class KeyboardInputModule extends InputModule {

    private static final Map<String, Integer> movementKeys = new HashMap<>();
    private final LinkedList<Condition> activeConditions = new LinkedList<>();
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<TickEvent> tickEventConsumer;
    //todo make these thresholds configurable and make them longer. Especially the timeout threshold.
    private final int tickActivationThreshold = 15;
    private final int tickTimeoutThreshold = 60;
    private final int modifierTickThreshold = 10;
    Minecraft mc = Minecraft.getInstance();
    private List<Integer> glfwKeysDown;
    private Form activeForm = new Form();
    private int ticksSinceActivated = 0;
    private int ticksSinceModifiersSent = 0;
    private boolean listen;

    // TODO: Fix this such that any tree requiring a form relies on the input
    // module activating a form rather than relying on the raw input data for those forms.
    // This way, the trees for different complex methods (such as VR and multikey)
    // remain functionally the same, they just check different input modules for whether the same
    // forms are activated.
    public KeyboardInputModule() {
        this.glfwKeysDown = new ArrayList<>();
        this.listen = true;
        this.keyboardListener = keyboardEvent -> {
            int keyPressed = keyboardEvent.getKey();
            // Get the Conditions list if one already exists for that key

            switch (keyboardEvent.getAction()) {
                case InputConstants.PRESS, InputConstants.REPEAT -> {
                    if (!glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.add(keyPressed);
                        checkForForm();
                    }
                }
                // NOTE: Minecraft's InputEvent.Key can only listen to the action InputConstants.REPEAT of one key at a time
                // tldr: it only fires the repeat event for the last key
                case InputConstants.RELEASE -> {
                    if (glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.remove((Integer) keyPressed);
                        activeConditions.remove(keyPressed);
                        checkForForm();
                    }
                }
            }
        };

        this.tickEventConsumer = tickEvent -> {
            for (Condition condition : activeConditions) {
                KeyHoldCondition keyHoldCondition = (KeyHoldCondition) condition;
                if (glfwKeysDown.contains(keyHoldCondition.getKey())) {
                    keyHoldCondition.iterateDuration();
                }
            }

            ticksSinceModifiersSent++;
            if (ticksSinceModifiersSent > modifierTickThreshold && !modifierQueue.isEmpty()) {
                sendModifierData();
            }

            if (activeForm.name() != null) {
                ticksSinceActivated++;
                if (ticksSinceActivated >= tickActivationThreshold) {
                    if (lastActivatedForm != null)
                        LogManager.getLogger().info("LAST FORM ACTIVATED: " + lastActivatedForm.name() + " | FORM ACTIVATED: " + activeForm.name());
                    else LogManager.getLogger().info("FORM ACTIVATED: " + activeForm.name());
//                    MagusNetwork.sendToServer(new ConditionActivatedPacket(activeForm));
                    lastActivatedForm = activeForm;
                    activeForm = new Form();
                    ticksSinceActivated = 0;
                }
            } else {
                ticksSinceActivated++;
                if (ticksSinceActivated >= tickTimeoutThreshold) {
                    lastActivatedForm = null;
                    ticksSinceActivated = 0;
                }
            }
        };
    }

    public static void determineMotionKeys() {
        Arrays.stream(Minecraft.getInstance().options.keyMappings).toList().forEach(keyMapping -> {
            if (keyMapping.getCategory().equals(KeyMapping.CATEGORY_MOVEMENT)) {
                movementKeys.put(keyMapping.getName(), keyMapping.getKey().getValue());
            }
        });
    }

    public static Map<String, Integer> getMovementKeys() {
        return movementKeys;
    }

    private void checkForForm() {
        List<Condition> conditions = activeConditions.stream().toList();
        List<Condition> recognized = formsTree.search(conditions);
        if (recognized != null) {
            // TODO: Clear activeConditions.
            Form form = FormDataRegistry.formsNamespace.get(recognized.hashCode());
            System.out.println("RECOGNIZED FORM: " + form.name() + " " + recognized);
        }
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
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute, List<Condition> formCondition) {
        // TODO:
        // - Change formCondition to be a list of Conditions.
        // - Have a createConditionPath(List<Condition> conditions) method for each Form.
        // - Call it here, then add the condition path to the radixtree.

        // Now, we call:
        System.out.println("Inserting " + formToExecute.name().toUpperCase() + " into tree with Conditions: " + formCondition + " | Inputs: " + formExecutionInputs);
        List<Condition> updatedConditions = formCondition.stream().toList();
        for (Condition condition : updatedConditions) {
            condition.register(condition.name(), () -> {
                condition.onSuccess();
                // Just do this instead and it's so much easier...
                if (!activeConditions.contains(condition))
                    activeConditions.add(condition);
            }, condition.onFailure());
        }
        ConditionPath path = formToExecute.createPath(updatedConditions);
        formsTree.insert(path.conditions);
        // add the path to the tree

//        //generate condition from InputData.
//        Runnable onSuccess = () -> {
//            if(mc.level != null) {
//                //this section is to prevent re-activating
//                // single condition forms when you hold the activation key for Held modifiers
//
//                //TODO: Fix an issue where it doesn't let players re-activate forms outside of the held modifier.
//                // I.e account for modifiers here.
//                if(formToExecute != lastActivatedForm) {
//                    //LogManager.getLogger().info("FORM ACTIVATED: " + formToExecute.name());
//                    activeForm = formToExecute;
//                }
//
//                ticksSinceActivated = 0;
//            }
//            //Reset condition
//        };
//        Runnable onFailure = () -> {
//            activeForm = new Form();
//            //reset conditions?
//           // Magus.radixTree.burn();
//        };
//
//        if(formCondition != null) {
//            //Register listeners for condition created.
//            formCondition.register(formToExecute.name(), onSuccess, onFailure);
//            //add condition to InputModule registry so that it can be tracked.
//            formInputs.put(formCondition, formToExecute);
//        } else {
//            //todo errors/logging
//        }

    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.Key.class, keyboardListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.class, tickEventConsumer);
    }

    @Override
    public void unRegisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(keyboardListener);
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        formInputs.forEach((condition, form) -> condition.unregister());
    }

    @Override
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
        } else {
            unRegisterInputs();
            listen = false;
        }
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.contains(key);
    }

    public boolean isDirectionKey(int key) {
        return movementKeys.containsValue(Integer.valueOf(key));
    }
}
