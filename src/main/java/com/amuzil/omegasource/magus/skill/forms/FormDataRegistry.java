package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;

import java.util.ArrayList;
import java.util.List;

public class FormDataRegistry {

    //Remember to see #InputConstants for the key names.
    public static void init() {
        List<InputData> inputs = new ArrayList<>();
        inputs.add(new KeyInput(InputConstants.getKey("key.mouse.right"),
                0, 0, 0));
        Magus.inputModule.registerInputData(inputs, Forms.STRIKE);
    }
}
