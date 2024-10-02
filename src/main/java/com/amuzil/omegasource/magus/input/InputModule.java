package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.elements.Discipline;
import com.amuzil.omegasource.magus.skill.elements.Disciplines;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.event.InputEvent;
import org.apache.logging.log4j.LogManager;

import java.util.*;


public abstract class InputModule {
    protected static RadixTree formsTree = new RadixTree(RadixTree.Side.CLIENT);
    protected static LinkedList<Condition> activeConditions = new LinkedList<>();
    protected static LinkedList<Form> activeForms = new LinkedList<>();
    protected static final List<Form> activeFormInputs = new ArrayList<>();
    protected final Map<Condition, Form> formInputs = new HashMap<>();
    protected final List<ModifierListener> modifierListeners = new ArrayList<>();
    protected final Map<String, ModifierData> modifierQueue = new HashMap<>();
    protected Form lastActivatedForm = null;

    public abstract void registerInputData(List<InputData> formExecutionInputs, Form formToExecute, List<Condition> conditions);

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
            if(modifierQueue.get(data.getName()) != null) {
                ModifierData existingData = modifierQueue.get(data.getName());
                existingData.add(data);
                modifierQueue.put(data.getName(), existingData);
            } else {
                modifierQueue.put(data.getName(), data);
            }
        }
    }

    public static EventCondition<?> keyToCondition(InputConstants.Key key, int actionCondition) {
        if (key.getType().equals(InputConstants.Type.MOUSE)) {
            return new EventCondition<>(InputEvent.MouseButton.class,
                    event -> event.getButton() == key.getValue() &&
                    event.getAction() == actionCondition);
        }
        else return new EventCondition<>(InputEvent.Key.class,
                event -> event.getKey() == key.getValue() && event.getAction()
        == actionCondition);
    }

    public void resetLastActivated() {
        LogManager.getLogger().info("RESETTING LAST ACTIVATED FORM");
        this.lastActivatedForm = null;
    }

    public Form getLastActivatedForm() {
        return this.lastActivatedForm;
    }

    public RadixTree getFormsTree() {
        return formsTree;
    }

    public static void resetFormsTree() {
        FormDataRegistry.init(); // Re-initialize formData since it's a static field
        formsTree = new RadixTree();
        // Default is air.
        formsTree.setDiscipline(Disciplines.AIR);
    }

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
