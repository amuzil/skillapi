package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.*;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.elements.Disciplines;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


public class KeyboardMouseInputModule extends InputModule {

    private final Consumer<TickEvent> tickEventConsumer;
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<InputEvent.MouseButton> mouseListener;
    private final Consumer<InputEvent.MouseScrollingEvent> mouseScrollListener;
    private final int tickActivationThreshold = 15;
    private final int tickTimeoutThreshold = 60;
    private final int modifierTickThreshold = 10;
    // Maybe?
    private final AtomicInteger ticksSinceActivated;
    private final AtomicReference<Form> activeForm;
    private final AtomicInteger timeout;
    private final List<Integer> glfwKeysDown;
    private int ticksSinceModifiersSent = 0;
    private double mouseScrollDelta;
    private int scrollTimeout = 0;
    private boolean listen;
    // Used for modifier data
    private boolean checkForm = false;

    // module activating a form rather than relying on the raw input data for those forms.
    // This way, the trees for different complex methods (such as VR and multikey)
    // remain functionally the same, they just check different input modules for whether the same
    // forms are activated.

    public KeyboardMouseInputModule() {
        formsTree.setDiscipline(Disciplines.AIR);

        this.ticksSinceActivated = new AtomicInteger(0);
        this.activeForm = new AtomicReference<>(Forms.NULL);
        this.timeout = new AtomicInteger(0);
        this.glfwKeysDown = new LinkedList<>();
        this.listen = true;
        this.resetScrolling = false;

        this.keyboardListener = keyboardEvent -> {
            int keyPressed = keyboardEvent.getKey();
            // NOTE: Minecraft's InputEvent.Key can only listen to the action InputConstants.REPEAT of one key at a time
            // tldr: it only fires the repeat event for the last key

            switch (keyboardEvent.getAction()) {
                case InputConstants.PRESS -> {
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

        this.mouseListener = mouseEvent -> {
            int keyPressed = mouseEvent.getButton();
            synchronized (glfwKeysDown) {
                switch (mouseEvent.getAction()) {
                    case InputConstants.PRESS -> {
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
            }
        };

        this.mouseScrollListener = mouseScrollingEvent -> {
            this.scrollTimeout = 0;
            // 1.0 = away from player, -1 = towards player
            this.mouseScrollDelta = mouseScrollingEvent.getScrollDelta();

        };

        this.tickEventConsumer = tickEvent -> {
            ticksSinceModifiersSent++;
            scrollTimeout++;

            // Resets mouse scrolling delta
            if (scrollTimeout >= tickActivationThreshold) {
                this.mouseScrollDelta = 0;
                this.scrollTimeout = 0;
                this.resetScrolling = true;
            }

            if (ticksSinceModifiersSent > modifierTickThreshold && !modifierQueue.isEmpty()) {
                sendModifierData();
            }

            // Every tick... Yay...
            // Needed so that key releases actually work.
            if (checkForm) {
                checkForForm();
                checkForm = false;
            }

            // Check every couple of ticks
            if (timeout.get() % 5 == 0) checkForForm();


            if (activeForm.get() != null && !activeForm.get().name().equals("null")) {
                ticksSinceActivated.getAndIncrement();
                if (ticksSinceActivated.get() >= tickActivationThreshold) {
                    // Always to send modifier data right when the form is activated
                    sendModifierData();

//                    if (lastActivatedForm != null && lastActivatedForm.name().equals(activeForm.name())) {
//                        // Send modifier data of it being multi.
//                    }
//                    else {
//                        // Send packet
//                        MagusNetwork.sendToServer(new ConditionActivatedPacket(activeForm));
//                    }

                    lastActivatedForm.set(activeForm.get());
                    // Extra check for race conditions. Probably wont' help...
                    synchronized (lastActivatedForm.get()) {
                        if (!lastActivatedForm.get().name().equals("null"))
                            Magus.sendDebugMsg("Form Activated: " + lastActivatedForm.get().name());
                    }
                    activeForm.set(Forms.NULL);
                    ticksSinceActivated.set(0);
                    timeout.set(0);
                    resetTreeConditions();
                }

            } else {
                timeout.getAndIncrement();
                if (timeout.get() > tickTimeoutThreshold) {
                    resetTreeConditions();
                    // Timed out enough where multi is no longer valid.
                    lastActivatedForm.set(Forms.NULL);
                    timeout.set(0);
                }
            }
        };
    }

    private void checkForForm() {
        synchronized (activeConditions) {
            if (!activeConditions.isEmpty()) {
                List<Condition> conditions = activeConditions.stream().toList();
                List<Condition> recognized = formsTree.search(conditions);
//            System.out.println("activeConditions: " + conditions);
//            System.out.println("recognized: " + recognized);
                if (recognized != null) {
                    activeForm.set(FormDataRegistry.formsNamespace.get(recognized.hashCode()));
//                System.out.println("RECOGNIZED FORM: " + activeForm.name() + " " + recognized);
//                Magus.sendDebugMsg("RECOGNIZED FORM: " + activeForm.name());
                }
            }
        }
    }

    private void sendModifierData() {
        LogManager.getLogger().info("SENDING MODIFIER DATA");
        System.out.println("Modifier Listener Size: " + modifierListeners.size());
        System.out.println("Modifier Data Size: " + modifierQueue.size());
        for (ModifierData modData : modifierQueue.values())
            modData.print();

        synchronized (modifierQueue) {
            MagusNetwork.sendToServer(new SendModifierDataPacket(modifierQueue.values().stream().toList()));
            ticksSinceModifiersSent = 0;
            modifierQueue.clear();
        }
    }

    public void resetKeys() {
        glfwKeysDown.clear();
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
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute, List<Condition> formConditions) {
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
                    synchronized (activeConditions) {
                        if (!activeConditions.contains(condition)) {
                            activeConditions.add(condition);
                            condition.unregister(); // unregister parent to give child nodes a shot to be heard
                            if (nextCondition != null)
                                nextCondition.register(); // register next condition in the path if it exists
                            List<Condition> childConditions = branch.next.branches.keySet().stream().toList();
                            RadixTree.registerConditions(childConditions); // register any next of kin children if they exist
                        }
                        this.timeout.set(0);
                        if (originalSuccess != null) originalSuccess.run();
                    }
                };

                Runnable originalFailure = condition.onFailure();
                Runnable onFailure = () -> {
                    synchronized (activeConditions) {
                        activeConditions.remove(condition);
                        condition.reset();
                        if (originalFailure != null) originalFailure.run();
                    }
                };
                condition.register(condition.name(), onSuccess, onFailure);
            }
            registerRunnables(branch.next);
        }
    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.class, tickEventConsumer);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.Key.class, keyboardListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseButton.class, mouseListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseScrollingEvent.class, mouseScrollListener);
    }

    @Override
    public void unRegisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        MinecraftForge.EVENT_BUS.unregister(keyboardListener);
        MinecraftForge.EVENT_BUS.unregister(mouseListener);
        MinecraftForge.EVENT_BUS.unregister(mouseScrollListener);
        formInputs.forEach((condition, form) -> condition.unregister());
    }

    @Override
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

    public double getMouseScrollDelta() {
        return this.mouseScrollDelta;
    }


}
