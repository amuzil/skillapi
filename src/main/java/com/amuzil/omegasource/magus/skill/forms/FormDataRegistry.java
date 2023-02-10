package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDataRegistry {

    private static Map<List<InputData>, Form> formTypes;

    //Remember to see #InputConstants for the key names.
    public static void init() {
        formTypes = new HashMap<>();
        List<InputData> inputs = new ArrayList<>();
        inputs.add(new KeyInput(InputConstants.getKey("key.mouse.right"),
                0, 0, 0));
        Magus.inputModule.registerInputData(inputs, Forms.STRIKE);
        formTypes.put(inputs, Forms.STRIKE);
    }

    public static Form getFormByName(String formName) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formName)).findFirst().get().getValue();
    }

    public static List<InputData> getInputsForForm(Form formToModify) {
        return formTypes.entrySet().stream().filter(form -> form.getValue().name().equals(formToModify.name())).findFirst().get().getKey();

    }
}
