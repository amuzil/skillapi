package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.*;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.elements.Disciplines;
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
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<TickEvent> tickEventConsumer;
    //todo make these thresholds configurable and make them longer. Especially the timeout threshold.
    private final int tickActivationThreshold = 15;
    private final int tickTimeoutThreshold = 60;
    private final int modifierTickThreshold = 10;
    public List<Condition> testConditions;
    public int testKey = 68;
    Minecraft mc = Minecraft.getInstance();
    private List<Integer> glfwKeysDown;
    private Form activeForm, lastActivatedForm = null;
    private int ticksSinceActivated = 0;
    private int ticksSinceModifiersSent = 0;
    private int timeout = 0;
    private boolean listen;
    private boolean checkForm = false;

    // TODO: Fix this such that any tree requiring a form relies on the input
    // module activating a form rather than relying on the raw input data for those forms.
    // This way, the trees for different complex methods (such as VR and multikey)
    // remain functionally the same, they just check different input modules for whether the same
    // forms are activated.
    public KeyboardInputModule() {
        formsTree.setDiscipline(Disciplines.AIR);

        this.glfwKeysDown = new ArrayList<>();
        this.listen = true;
        this.keyboardListener = keyboardEvent -> {
            int keyPressed = keyboardEvent.getKey();
            // NOTE: Minecraft's InputEvent.Key can only listen to the action InputConstants.REPEAT of one key at a time
            // tldr: it only fires the repeat event for the last key

            switch (keyboardEvent.getAction()) {
                case InputConstants.PRESS-> {
                    if (!glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.add(keyPressed);
//                        checkForForm();
                    }
                }
                case InputConstants.RELEASE -> {
                    if (glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.remove((Integer) keyPressed);
//                        checkForForm();
                    }
                }
            }
        };

        this.tickEventConsumer = tickEvent -> {
            ticksSinceModifiersSent++;
            if (ticksSinceModifiersSent > modifierTickThreshold && !modifierQueue.isEmpty()) {
                sendModifierData();
            }

            // Every tick... Yay...
            // Needed so that key releases actually work.
            if (checkForm) {
                checkForForm();
                checkForm = false;
            }

            if (activeForm != null && activeForm.name() != null) {
                ticksSinceActivated++;
                if (ticksSinceActivated >= tickActivationThreshold) {
//                    if (lastActivatedForm != null)
//                        LogManager.getLogger().info("LAST FORM ACTIVATED: " + lastActivatedForm.name() + " | FORM ACTIVATED: " + activeForm.name());
//                    else LogManager.getLogger().info("FORM ACTIVATED: " + activeForm.name());
//                    MagusNetwork.sendToServer(new ConditionActivatedPacket(activeForm));
                    lastActivatedForm = activeForm;
                    activeForm = null;
                    ticksSinceActivated = 0;
                    timeout = 0;
                    resetConditions();

                }
            }

//            } else {
//                timeout++;
//                if (timeout > tickTimeoutThreshold) {
//                    if (!activeConditions.isEmpty()) {
//                        System.out.println("Resetting...");
//                        formsTree.resetTree();
//                        activeConditions.clear();
//                        lastActivatedForm = null;
//                        timeout = 0;
//                    }
//                }
//            }
            //else {
            ticksSinceActivated++;
            if (ticksSinceActivated >= tickTimeoutThreshold) {
//                    System.out.println("RESET STATE!");
                lastActivatedForm = null;
                resetConditions();
                formsTree.resetTree();
                ticksSinceActivated = 0;
            }
        };
    }

    public void resetConditions() {
        if (!activeConditions.isEmpty()) {
            for (Condition condition : activeConditions)
                condition.reset();
            activeConditions.clear();
        }
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
        if (!activeConditions.isEmpty()) {
            System.out.println("activeConditions KIM: " + activeConditions + " | " + glfwKeysDown);
            List<Condition> conditions = activeConditions.stream().toList();
            List<Condition> recognized = formsTree.search(conditions);
            System.out.println("FOUND STEP D FORM: " + formsTree.search(testConditions));
//            System.out.println("STEP SNEAK FORM: " + testConditions);
//            testConditions.forEach(condition -> System.out.print(condition.getActiveStatus() + " "));
//            System.out.println();
//            formsTree.printAllConditions();
            if (recognized != null) {
                resetConditions();
                activeForm = FormDataRegistry.formsNamespace.get(recognized.hashCode());
                System.out.println("RECOGNIZED FORM: " + activeForm.name() + " " + recognized);
                Magus.sendDebugMsg("RECOGNIZED FORM: " + activeForm.name());
            }
        }

        if (ticksSinceActivated >= tickTimeoutThreshold) {
            lastActivatedForm = null;
            activeConditions.clear();
            formsTree.resetTree();
            ticksSinceActivated = 0;
//            System.out.println("RESET");
        }
        ticksSinceActivated++;
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
//        List<Condition> updatedConditions = formCondition.stream().toList();
//        for (int i = 0; i < updatedConditions.size(); i++) {
//            Condition condition = updatedConditions.get(i);
//            Condition nextCondition;
//            if (i + 1 < updatedConditions.size()) nextCondition = updatedConditions.get(i + 1);
//            else nextCondition = null;
//            condition.register(condition.name(), () -> {
//                if (!activeConditions.contains(condition)) {
//                    activeConditions.add(condition);
//                    condition.unregister();
//                    if (nextCondition != null) nextCondition.register();
//                }
//                condition.reset();
//                this.timeout = 0;
//            }, () -> {
//                activeConditions.remove(condition);
//                condition.reset();
//            });
//        }

        ConditionPath path = formToExecute.createPath(formCondition);
        System.out.println("Inserting " + formToExecute.name().toUpperCase() + " into tree with Conditions: " + formCondition + " | Inputs: " + formExecutionInputs);
        formsTree.insert(path.conditions);
        registerRunnables(formsTree);
        if (formCondition.get(0) instanceof KeyHoldCondition keyCondition)
            if (keyCondition.getKey() == testKey)
                testConditions = formCondition;
    }

    public void registerRunnables(RadixTree tree) {
        registerRunnables(tree.getRoot());
    }

    @Override
    public void registerRunnables(Node current) {
        for (RadixBranch branch : current.branches.values()) {
            if (!branch.next.branches.keySet().isEmpty())
                System.out.println(branch.conditions() + " | THE KIDS: " + branch.next.branches.keySet());
            for (int i=0; i < branch.conditions().size(); i++) {
                Condition condition = branch.conditions().get(i);
                Condition nextCondition;
                if (i+1 < branch.conditions().size())
                    nextCondition = branch.conditions().get(i+1);
                else
                    nextCondition = null;
                Runnable onSuccess = () -> {
                    if (!activeConditions.contains(condition)) {
                        activeConditions.add(condition);
                        condition.unregister(); // unregister parent to give child nodes a shot to be heard
                        if (nextCondition != null)
                            nextCondition.register(); // register next condition in the path if it exists
                        List<Condition> childConditions = branch.next.branches.keySet().stream().toList();
                        RadixTree.registerConditions(childConditions); // register any next of kin if they exist
                    }
                    this.timeout = 0;
                };
                Runnable onFailure = () -> {
                    activeConditions.remove(condition);
                    condition.reset();
                };
                condition.register(condition.name(), onSuccess, onFailure);
            }
            registerRunnables(branch.next);
        }
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
