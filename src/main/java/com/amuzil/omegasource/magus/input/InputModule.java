package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.Node;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.elements.Disciplines;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.event.InputEvent;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public abstract class InputModule {
    public boolean resetScrolling;
    protected static final List<Form> activeFormInputs = new ArrayList<>();
    protected static final Map<String, Integer> movementKeys = new HashMap<>();
    protected static RadixTree formsTree = new RadixTree();
    protected static LinkedList<Form> activeForms = new LinkedList<>();
    protected final List<Condition> activeConditions = Collections.synchronizedList(new LinkedList<>());
    protected final Map<Condition, Form> formInputs = new HashMap<>();
    protected final List<ModifierListener> modifierListeners = new ArrayList<>();
    protected final Map<String, ModifierData> modifierQueue = new HashMap<>();
    protected AtomicReference<Form> lastActivatedForm = new AtomicReference<>(Forms.NULL);

    public static EventCondition<?> keyToCondition(InputConstants.Key key, int actionCondition) {
        if (key.getType().equals(InputConstants.Type.MOUSE)) {
            return new EventCondition<>(InputEvent.MouseButton.class,
                    event -> event.getButton() == key.getValue() &&
                            event.getAction() == actionCondition);
        } else return new EventCondition<>(InputEvent.Key.class,
                event -> event.getKey() == key.getValue() && event.getAction()
                        == actionCondition);
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

    public static boolean isDirectionKey(int key) {
        return movementKeys.containsValue(Integer.valueOf(key));
    }

    public static void resetFormsTree() {
        FormDataRegistry.init(); // Re-initialize formData since it's a static field
        formsTree = new RadixTree();
        // Default is air.
        formsTree.setDiscipline(Disciplines.AIR);
    }

    public abstract void registerInputData(List<InputData> formExecutionInputs, Form formToExecute, List<Condition> conditions);

    public void registerModifiers() {
        registerModifierListener(ModifiersRegistry.CONTROL.listener(), Minecraft.getInstance().player.getPersistentData());
        // Need to register this to the player's compound tag...
//        for (Modifier modifiers : ModifiersRegistry.getModifiers()) {
//            registerModifierListener(modifiers.listener(), Minecraft.getInstance().player.getPersistentData());
//        }
    }
    public void registerModifierListener(ModifierListener listener, CompoundTag treeData) {
        listener.setupListener(treeData);
        listener.register(() -> {
            LogManager.getLogger().info("QUEUEING MODIFIER DATA");
            queueModifierData(listener.getModifierData());
        });

        modifierListeners.add(listener);
    }

    public synchronized void queueModifierData(ModifierData data) {
        synchronized (modifierQueue) {
            if (modifierQueue.get(data.getName()) != null) {
                ModifierData existingData = modifierQueue.get(data.getName());
                existingData.add(data);
                modifierQueue.put(data.getName(), existingData);
            } else {
                modifierQueue.put(data.getName(), data);
            }
        }
    }

    public void resetLastActivated() {
        LogManager.getLogger().info("RESETTING LAST ACTIVATED FORM");
        this.lastActivatedForm = null;
    }

    public Form getLastActivatedForm() {
        return this.lastActivatedForm.get();
    }

    public List<Condition> getActiveConditions() {
        return activeConditions;
    }

    public RadixTree getFormsTree() {
        return formsTree;
    }

    public void resetTreeConditions() {
        resetConditions();
        formsTree.resetTree();
    }

    public void resetConditions() {
        if (!activeConditions.isEmpty()) {
            for (Condition condition : activeConditions)
                condition.reset();
            activeConditions.clear();
        }
    }

//    private Form checkForForm() {
//        if (!activeConditions.isEmpty()) {
//            List<Condition> conditions = activeConditions.stream().toList();
//            List<Condition> recognized = formsTree.search(conditions);
//            if (recognized != null) {
//                return FormDataRegistry.formsNamespace.get(recognized.hashCode());
//                System.out.println("RECOGNIZED FORM: " + activeForm.name() + " " + recognized);
//                Magus.sendDebugMsg("RECOGNIZED FORM: " + activeForm.name());
//            }
//        }
//        return new Form();
//    }

    public void init() {
        resetKeys();
        registerInputs();
        registerListeners();
    }

    public void terminate() {
        resetKeys();
        unRegisterInputs();
        activeConditions.clear();
    }

    public void registerRunnables(RadixTree tree) {
        registerRunnables(tree.getRoot());
    }

    public abstract void registerRunnables(Node current);

    public abstract void registerListeners();

    public abstract void registerInputs();

    public abstract void unRegisterInputs();

    public void unregisterModifiers() {
        modifierListeners.forEach(ModifierListener::unregister);
    }

    public abstract void toggleListeners();

    public abstract void resetKeys();

    public abstract boolean keyPressed(int key);


}
