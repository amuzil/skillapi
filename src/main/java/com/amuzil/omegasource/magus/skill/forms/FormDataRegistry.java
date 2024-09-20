package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.*;

public class FormDataRegistry {

    //TODO: Change this to use Conditions rather than a list of input data, and use a hashmap of input types
    private static Map<List<InputData>, Form> formTypes;
    private static Map<Form, List<Condition>> formConditions;

    //Remember to see #InputConstants for the key names.
    public static void init() {
        formTypes = new HashMap<>();
        formConditions = new HashMap<>();
    }

    public static List<InputData> getInputsForForm(Form formToModify, RadixTree.InputType type) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formToModify.name())).findFirst().get().getKey();
    }


    public static void registerForm(List<InputData> inputs, Form form, RadixTree.InputType type) {
        // Register the requisite conditions
        List<Condition> conditions;
        if (formConditions.containsKey(form)) {
            conditions = formConditions.get(form);
        }
        else conditions = new LinkedList<>();
        Condition condition = ConditionBuilder.instance().fromInputData(inputs).build();
        conditions.add(condition);
        formConditions.put(form, conditions);
        // Register the raw input data
        switch (type) {
            // I can dream...
            case VR -> {
            }
            case KEYBOARD -> Magus.keyboardInputModule.registerInputData(inputs, form, condition);
            case MOUSE -> Magus.mouseInputModule.registerInputData(inputs, form, condition);
            case MOUSE_MOTION -> Magus.mouseMotionModule.registerInputData(inputs, form, condition);

        }
    }

    public static List<Condition> getConditionsFrom(Form form) {
        return formConditions.get(form);
    }

    public static void registerForm(InputData input, Form form) {
        List<InputData> singleton = new ArrayList<>();
        singleton.add(input);
        registerForm(singleton, form, RadixTree.InputType.KEYBOARD);
    }
}
