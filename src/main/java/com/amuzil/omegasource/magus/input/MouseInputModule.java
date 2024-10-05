package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.*;
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


public class MouseInputModule extends InputModule {

    private final Consumer<TickEvent> tickEventConsumer;
    private final Consumer<InputEvent.MouseButton> mouseListener;
    private final Consumer<InputEvent.MouseScrollingEvent> mouseScrollListener;
    private final int tickActivationThreshold = 15;
    private final int tickTimeoutThreshold = 60;
    private final int modifierTickThreshold = 10;
    private int ticksSinceActivated = 0;
    private int ticksSinceModifiersSent = 0;
    private List<Integer> glfwKeysDown;
    private Form activeForm;
    private double mouseScrollDelta;
    private int timeout = 0;
    private boolean listen;
    private boolean checkForm = false;

    // How scroll delta works: every physical "tick" forwards on the mouse is 1.0, and backwards
    // is -1.0. Therefore, you'd need a tracker over time, like a key held event, for the mouse wheel.
    // Except you're not pressing it, you're spinning it....

    public MouseInputModule() {
        this.glfwKeysDown = new ArrayList<>();
        this.listen = true;

        this.mouseListener = mouseEvent -> {
            int keyPressed = mouseEvent.getButton();
            switch (mouseEvent.getAction()) {
                case InputConstants.PRESS-> {
                    if (!glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.add(keyPressed);
                    }
                }
                case InputConstants.RELEASE -> {
                    if (glfwKeysDown.contains(keyPressed)) {
                        glfwKeysDown.remove((Integer) keyPressed);
                        checkForm = true;
                    }
                }
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

            if (checkForm) {
                checkForForm();
                checkForm = false;
            }

            // Check every couple of ticks
            if (timeout % 5 == 0)
                checkForForm();

            if (activeForm != null && activeForm.name() != null) {
                ticksSinceActivated++;
                if(ticksSinceActivated >= tickActivationThreshold) {
//                    if (lastActivatedForm != null)
//                        LogManager.getLogger().info("LAST FORM ACTIVATED: " + lastActivatedForm.name() + " | FORM ACTIVATED: " + activeForm.name());
//                    else
//                        LogManager.getLogger().info("FORM ACTIVATED: " + activeForm.name());
//                    MagusNetwork.sendToServer(new ConditionActivatedPacket(activeForm));
                    lastActivatedForm = activeForm;
                    Magus.sendDebugMsg("Form Activated: " + lastActivatedForm.name());
                    activeForm = null;
                    ticksSinceActivated = 0;
                    timeout = 0;
                    resetTreeConditions();
                }
            } else {
                timeout++;
                if (timeout > tickTimeoutThreshold) {
                    resetTreeConditions();
                    // Timed out enough where multi is no longer valid.
                    lastActivatedForm = null;
                    timeout = 0;
                }
            }
        };
    }

    private void checkForForm() {
        if (!activeConditions.isEmpty()) {
            List<Condition> conditions = activeConditions.stream().toList();
            List<Condition> recognized = formsTree.search(conditions);
            if (recognized != null) {
                activeForm = FormDataRegistry.formsNamespace.get(recognized.hashCode());
//                System.out.println("RECOGNIZED FORM: " + activeForm.name() + " " + recognized);
//                Magus.sendDebugMsg("RECOGNIZED FORM: " + activeForm.name());
            }
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
    public void registerInputData(List<InputData> formExecutionInputs,
                                  Form formToExecute, List<Condition> formConditions) {
        ConditionPath path = formToExecute.createPath(formConditions);
        System.out.println("Inserting " + formToExecute.name().toUpperCase() + " into tree with Conditions: " + formConditions + " | Inputs: " + formExecutionInputs);
        formsTree.insert(path.conditions);
        registerRunnables(formsTree);
    }

    @Override
    public void registerRunnables(Node current) {
        for (RadixBranch branch : current.branches.values()) {
//            if (!branch.next.branches.keySet().isEmpty())
//                System.out.println(branch.conditions() + " | THE KIDS: " + branch.next.branches.keySet());
            for (int i = 0; i < branch.conditions().size(); i++) {
                Condition condition = branch.conditions().get(i);
                Condition nextCondition;

                if (i + 1 < branch.conditions().size()) nextCondition = branch.conditions().get(i + 1);
                else nextCondition = null;

                Runnable originalSuccess = condition.onSuccess();
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
                    if (originalSuccess != null)
                        originalSuccess.run();
                };

                Runnable originalFailure = condition.onFailure();
                Runnable onFailure = () -> {
                    activeConditions.remove(condition);
                    condition.reset();
                    if (originalFailure != null)
                        originalFailure.run();
                };
                condition.register(condition.name(), onSuccess, onFailure);
            }
            registerRunnables(branch.next);
        }
    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseButton.class, mouseListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseScrollingEvent.class, mouseScrollListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.class, tickEventConsumer);
    }

    @Override
    public void unRegisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(mouseListener);
        MinecraftForge.EVENT_BUS.unregister(mouseScrollListener);
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        formInputs.forEach((condition, form) -> condition.unregister());
    }

    public void registerInputs() {
        formInputs.forEach((condition, form) -> condition.register());
    }

    @Override
    public void toggleListeners() {
        if (!listen) {
            registerListeners();
            registerInputs();
            listen = true;
            System.out.println("Enabled!");
        } else {
            unRegisterInputs();
            listen = false;
            System.out.println("Disabled!");
        }
    }

    public boolean keyPressed(int key) {
        return glfwKeysDown.contains(key);
    }
}
