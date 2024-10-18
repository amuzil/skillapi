package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModuleData;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormDataRegistry {

    private static Map<Form, InputModuleData> formsData;
    public static HashMap<Integer, Form> formsNamespace;

    //Remember to see #InputConstants for the key names.
    public static void init() {
        formsData = new HashMap<>();
        formsNamespace = new HashMap<>();
    }

    public static Form getFormByName(String formName) {
        return Registries.FORMS.get().getValue(new ResourceLocation(formName));
    }

    public static List<InputData> getInputsForForm(Form formToModify, RadixTree.InputType type) {
        return formsData.get(formToModify).getInputs(type);
    }

    public static void registerForm(List<InputData> inputs, Form form, RadixTree.InputType type) {
        // Register the requisite conditions
        InputModuleData data = new InputModuleData();
        data.addTypeInputs(type, inputs);
        data.fillConditions(type);
        // This replaces the value, and since our InputModuleData automatically adds conditions and input data to itself when necessary...
        formsData.put(form, data);
        formsNamespace.put(data.getConditions(type).hashCode(), form);

        // Need to pass this
        List<Condition> conditions = data.getConditions(type);
        // Register the raw input data
        switch (type) {
            // I can dream...
            case VR -> {
            }
            case KEYBOARD_MOUSE -> Magus.keyboardMouseInputModule.registerInputData(inputs, form, conditions);
            case MOUSE_MOTION -> Magus.mouseMotionModule.registerInputData(inputs, form, conditions);

        }
    }

    public static List<Condition> getConditionsFrom(Form form, RadixTree.InputType type) {
        return formsData.get(form).getConditions(type);
    }

    public static void registerForm(InputData input, Form form) {
        List<InputData> singleton = new ArrayList<>();
        singleton.add(input);
        registerForm(singleton, form, RadixTree.InputType.KEYBOARD_MOUSE);
    }
}
