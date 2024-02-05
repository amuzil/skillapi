package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.InputDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;
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
        KeyInput held = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyShift.getKey(), 0, 0,
                20);
        KeyInput sneak = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyShift.getKey(), 0, 0,
                0);
        MouseWheelInput forwards = MouseDataBuilder.createWheelInput(MouseDataBuilder.Direction.FORWARDS, 1);
        MouseWheelInput back = MouseDataBuilder.createWheelInput(MouseDataBuilder.Direction.BACK, 1);

        LinkedList<InputData> inputs = new LinkedList<>();


        // UPDATE: Rather than having "push", "pull", "raise", and "lower" as Forms,
        // why not make them Effects using the Force form and Direction modifier?
        // TODO: Bring that up with other devs, seems like it makes more sense!

        //FormDataRegistry.registerForm(Forms.PUSH);

        //FormDataRegistry.registerForm(Forms.PULL);

        //FormDataRegistry.registerForm(Forms.RAISE);

        //FormDataRegistry.registerForm(Forms.LOWER);

        LinkedList<InputData> data = InputDataBuilder.toInputs(
                KeyDataBuilder.createMultiInput(left, right));
//
        FormDataRegistry.registerForm(data, Forms.BURST);
//
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(initialiser), Forms.ARC);
//
//        //TODO: Add mouse wheel input (once the input module supports it). Mouse wheel 1; forwards/away from the player.
//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak, forwards), Forms.COMPRESS);
//
//        //TODO: Add mouse wheel input (once the input module supports it). Mouse wheel -1; towards the player/backwards.
//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak, back), Forms.EXPAND);
//
//        // TODO: Use mouse motion, but the input module currently does not support that.
////        FormDataRegistry.registerForm(Forms.ROTATE);
//
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(left), Forms.STRIKE);

//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(right), Forms.FORCE);
//
//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(held), Forms.BREATHE);


        //Motion! 7 different movement options.
        // Left, right, up, down, jump, sneak, sprint.

        //TODO: Add in a mandatory key release condition so you can't activate these by holding down a key.
        // Also account for the direction modifier.
        for (Map.Entry<String, Integer> key : KeyboardMouseInputModule.getMovementKeys().entrySet()) {
            inputs.clear();
            //TODO: Find a way to specify releasing a key is required.

            KeyInput first, release, second;
            // Problem: The tick delay is being combined with the key press into a simultaneous condition.
            // Not good.
            first = KeyDataBuilder.createInput(key.getValue(), true,60, 70, 0);
            second = KeyDataBuilder.createInput(key.getValue(), 0, 0, 0);
            inputs.add(KeyDataBuilder.createChainedInput(first, second));
            FormDataRegistry.registerForm(inputs, Forms.STEP);
        }
    }
}
