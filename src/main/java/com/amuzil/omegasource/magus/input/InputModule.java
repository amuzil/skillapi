package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.event.InputEvent;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InputModule {
    protected final Map<Condition, Form> _formInputs = new HashMap<>();
    protected final List<ModifierListener> _modifierListeners = new ArrayList<>();
    protected final Map<String, ModifierData> modifierQueue = new HashMap<>();
    protected Form lastActivatedForm = null;

    public abstract void registerInputData(List<InputData> formExecutionInputs, Form formToExecute);

    public void registerModifierListener(ModifierListener listener, CompoundTag treeData) {
        listener.setupListener(treeData);
        listener.register(() -> {
            LogManager.getLogger().info("QUEUEING MODIFIER DATA");
            queueModifierData(listener.getModifierData());
        });

        _modifierListeners.add(listener);
    }

    public void queueModifierData(ModifierData data) {
        if(modifierQueue.get(data.getName()) != null) {
            ModifierData existingData = modifierQueue.get(data.getName());
            existingData.add(data);
            modifierQueue.put(data.getName(), existingData);
        } else {
            modifierQueue.put(data.getName(), data);
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
        this.lastActivatedForm = null;
    }

    public abstract void unregisterInputs();

    public void unregisterModifiers() {
        _modifierListeners.forEach(ModifierListener::unregister);
    }
}
