package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.forms.Form;

public class KeyboardMouseInputModule extends InputModule {

    @Override
    public void registerInputData(Condition formCondition, Form formToExecute, Runnable onSuccess, Runnable onFailure) {
        if(formCondition != null) {
            //Register listeners for condition created.
            formCondition.register(onSuccess, onFailure);
            //add condition to InputModule registry so that it can be tracked.
            _formInputs.put(formCondition, formToExecute);
        } else {
            //todo errors/logging
        }
    }
}
