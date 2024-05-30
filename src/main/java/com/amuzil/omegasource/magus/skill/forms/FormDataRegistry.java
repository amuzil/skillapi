package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDataRegistry {

    //TODO: Change this to use Conditions rather than a list of input data
    private static Map<List<InputData>, Form> formTypes;
    private static Map<Form, List<Condition>> formConditions;

    //Remember to see #InputConstants for the key names.
    public static void init() {
        formTypes = new HashMap<>();
        formConditions = new HashMap<>();
    }

    public static Form getFormByName(String formName) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formName)).findFirst().get().getValue();
    }

    public static List<InputData> getInputsForForm(Form formToModify) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formToModify.name())).findFirst().get().getKey();
    }

    public static void registerForm(List<InputData> inputs, Form form) {
        // First, we register the raw input data
        Magus.keyboardInputModule.registerInputData(inputs, form);
        formTypes.put(inputs, form);
        // Then, we register the requisite conditions
        List<Condition> conditions = new ArrayList<>();
        if (formConditions.containsKey(form)) {
            conditions = formConditions.get(form);
        }
        Condition condition = ConditionBuilder.instance().fromInputData(inputs).build();
        conditions.add(condition);
        formConditions.put(form, conditions);
    }

    public static List<Condition> getConditionsFrom(Form form) {
        return formConditions.get(form);
    }
    public static void registerForm(InputData input, Form form) {
        List<InputData> singleton = new ArrayList<>();
        singleton.add(input);
        registerForm(singleton, form);
    }
}
