package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.List;

public class FormDataRegistry {

    private static List<Form> formTypes;

    //Remember to see #InputConstants for the key names.
    public static void init() {
        formTypes = new ArrayList<>();
        List<InputData> inputs = new ArrayList<>();
        inputs.add(new KeyInput(InputConstants.getKey("key.mouse.right"),
                0, 0, 0));
        Magus.inputModule.registerInputData(inputs, Forms.STRIKE);
        formTypes.add(Forms.STRIKE);
    }

    public static Form getFormByName(String formName) {
        return formTypes.stream().filter(form -> form.name().equals(formName)).findFirst().get();
    }
}
