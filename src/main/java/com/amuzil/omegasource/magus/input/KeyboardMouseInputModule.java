package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.List;

public class KeyboardMouseInputModule extends InputModule {

    private RadixTree tree;

    @Override
    public Condition registerInputData(Condition formCondition, Form formToExecute, Runnable onSuccess, Runnable onFailure) {
        if(formCondition != null) {
            //Register listeners for condition created.
            formCondition.register(onSuccess, onFailure);
            //add condition to InputModule registry so that it can be tracked.
            _formInputs.put(formCondition, formToExecute);

            return formCondition;
        } else {
            //todo errors/logging
            return null;
        }
    }
}
