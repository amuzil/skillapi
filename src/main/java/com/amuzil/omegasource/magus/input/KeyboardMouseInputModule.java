package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.List;

public class KeyboardMouseInputModule extends InputModule {

    @Override
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute) {
        //generate condition from InputData.
        Runnable onSuccess = () -> {
            //todo pass formToExecute to the form queue.

            //reset condition?
        };
        Runnable onFailure = () -> {
            //reset conditions?
        };
        Condition formCondition = new ConditionBuilder()
                .fromInputData(formExecutionInputs)
                .build();
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
