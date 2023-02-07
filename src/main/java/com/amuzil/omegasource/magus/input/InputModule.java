package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.InputEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InputModule {
    protected final Map<Condition, Form> _formInputs = new HashMap<>();

    public abstract void registerInputData(List<InputData> formExecutionInputs, Form formToExecute);

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

    public abstract void unregister();
}
