package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyCombination;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyPermutation;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.mojang.blaze3d.platform.InputConstants;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AvatarFormRegistry {

    public static void registerForms() {
        KeyInput left = KeyDataBuilder.createInput(InputConstants.getKey("key.mouse.left"), 0,
                0, 0);
        KeyInput right = KeyDataBuilder.createInput(InputConstants.getKey("key.mouse.right"), 0,
                0, 0);
        LinkedList<InputData> inputs = new LinkedList<>();
        KeyPermutation permutation;
        KeyCombination combination;


        //FormDataRegistry.registerForm(Forms.PUSH);

        //FormDataRegistry.registerForm(Forms.PULL);

        //FormDataRegistry.registerForm(Forms.RAISE);

        //FormDataRegistry.registerForm(Forms.LOWER);

        inputs.clear();
        inputs.add(KeyDataBuilder.createPermutation(left, right));
        FormDataRegistry.registerForm(inputs, Forms.BURST);

      //  FormDataRegistry.registerForm(Forms.ARC);

    //    FormDataRegistry.registerForm(Forms.COMPRESS);

  //      FormDataRegistry.registerForm(Forms.EXPAND);

//        FormDataRegistry.registerForm(Forms.ROTATE);

        inputs.clear();
        inputs.add(left);
        FormDataRegistry.registerForm(inputs, Forms.STRIKE);

        inputs.clear();
        inputs.add(right);
        FormDataRegistry.registerForm(inputs, Forms.FORCE);

     //   FormDataRegistry.registerForm(Forms.BREATHE);


        //Motion! 7 different movement options.
        // Left, right, up, down, jump, sneak, sprint.
        for (Map.Entry<String, Integer> key : KeyboardMouseInputModule.getMovementKeys().entrySet()) {
            inputs.clear();
            KeyInput first, second;
            first = KeyDataBuilder.createInput(InputConstants.getKey(key.getKey()), 0, 10, 0);
            second = KeyDataBuilder.createInput(InputConstants.getKey(key.getKey()), 0, 0, 0);
            inputs.add(KeyDataBuilder.createCombination(first, second));
            FormDataRegistry.registerForm(inputs, Forms.STEP);
        }

    }
}
