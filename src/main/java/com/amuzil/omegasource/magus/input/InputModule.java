package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.ModifierListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InputModule {
    protected final Map<Condition, Form> _formInputs = new HashMap<>();
    protected final Map<Condition, ModifierListener> _modifierConditions = new HashMap<>();

    public abstract void registerInputData(List<InputData> formExecutionInputs, Form formToExecute);
}
