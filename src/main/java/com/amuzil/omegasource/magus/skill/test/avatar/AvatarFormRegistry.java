package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.InputDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.Map;

public class AvatarFormRegistry {

    public static void registerForms() {
        KeyInput left = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyAttack.getKey(), 0,
                0, 0);
        KeyInput right = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyUse.getKey(), 0,
                0, 0);
        KeyInput initialiser = KeyDataBuilder.createInput("key.keyboard.left.alt", 0, 0, 0);
        LinkedList<InputData> inputs = new LinkedList<>();


        //FormDataRegistry.registerForm(Forms.PUSH);

        //FormDataRegistry.registerForm(Forms.PULL);

        //FormDataRegistry.registerForm(Forms.RAISE);

        //FormDataRegistry.registerForm(Forms.LOWER);

        LinkedList<InputData> data = InputDataBuilder.toInputs(
                KeyDataBuilder.createPermutation(left, right));

        FormDataRegistry.registerForm(data, Forms.BURST);

        FormDataRegistry.registerForm(InputDataBuilder.toInputs(initialiser), Forms.ARC);

        //    FormDataRegistry.registerForm(Forms.COMPRESS);

        //      FormDataRegistry.registerForm(Forms.EXPAND);

//        FormDataRegistry.registerForm(Forms.ROTATE);

        FormDataRegistry.registerForm(InputDataBuilder.toInputs(left), Forms.STRIKE);

        FormDataRegistry.registerForm(InputDataBuilder.toInputs(right), Forms.FORCE);

        //   FormDataRegistry.registerForm(Forms.BREATHE);


        //Motion! 7 different movement options.
        // Left, right, up, down, jump, sneak, sprint.

        //TODO: Add in a mandatory key release condition so you can't activate these by holding down a key.
        // Also account for the direction modifier.
        for (Map.Entry<String, Integer> key : KeyboardMouseInputModule.getMovementKeys().entrySet()) {
            inputs.clear();
            KeyInput first, second;
            first = KeyDataBuilder.createInput(key.getValue(), 0, 10, 0);
            second = KeyDataBuilder.createInput(key.getValue(), 0, 0, 0);
            inputs.add(KeyDataBuilder.createCombination(first, second));
            FormDataRegistry.registerForm(inputs, Forms.STEP);
        }
    }
}
