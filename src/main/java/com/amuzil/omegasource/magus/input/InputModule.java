package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.HashMap;
import java.util.Map;

public abstract class InputModule {
    protected final Map<Condition, Form> _formInputs = new HashMap<>();

    public abstract void registerInputData(Condition formCondition, Form formToExecute, Runnable onSuccess, Runnable onFailure);

    public void unregisterCondition(Condition condition) {
        condition.unregister();
        _formInputs.remove(condition);
    }
}
