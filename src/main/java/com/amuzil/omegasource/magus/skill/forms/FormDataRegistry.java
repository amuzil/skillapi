package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDataRegistry {

    private static Map<List<InputData>, Form> formTypes;

    //Remember to see #InputConstants for the key names.
    public static void init() {
        formTypes = new HashMap<>();
    }

    public static Form getFormByName(String formName) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formName)).findFirst().get().getValue();
    }

    public static List<InputData> getInputsForForm(Form formToModify) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formToModify.name())).findFirst().get().getKey();
    }

    public static void registerForm(List<InputData> inputs, Form form) {
        Magus.keyboardInputModule.registerInputData(inputs, form);
        formTypes.put(inputs, form);
        Registries.registerForm(form);
    }

    public static void registerForm(InputData input, Form form) {
        List<InputData> singleton = new ArrayList<>();
        singleton.add(input);
        registerForm(singleton, form);
    }
}
