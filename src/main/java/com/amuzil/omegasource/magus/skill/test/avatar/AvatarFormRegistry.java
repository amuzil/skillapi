package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.input.KeyboardInputModule;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.InputDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseDataBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.Map;

public class AvatarFormRegistry {

    public static void registerForms() {
        KeyInput left = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyAttack.getKey(), 0);
        KeyInput right = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyUse.getKey(), 0);
        KeyInput initializer = KeyDataBuilder.createInput("key.keyboard.left.alt", 0);
        KeyInput held = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyShift.getKey(), 20);
        KeyInput sneak = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyShift.getKey(), 0);
        MouseWheelInput forwards = MouseDataBuilder.createWheelInput(MouseDataBuilder.Direction.FORWARDS, 1);
        MouseWheelInput back = MouseDataBuilder.createWheelInput(MouseDataBuilder.Direction.BACK, 1);


        // TODO: Rather than having "push", "pull", "raise", and "lower" as Forms,
        //  make them Effects using the Force Form, Direction modifier & Target Modifier

        FormDataRegistry.registerForm(InputDataBuilder.toInputs(left), Forms.STRIKE, InputConstants.Type.MOUSE);
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(right), Forms.FORCE, InputConstants.Type.MOUSE);
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(initializer), Forms.ARC, InputConstants.Type.KEYSYM);
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(KeyDataBuilder.createMultiInput(left, right)), Forms.BURST, InputConstants.Type.MOUSE);

//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak, forwards), Forms.COMPRESS);
//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak, back), Forms.EXPAND);
//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(held), Forms.BREATHE);
//        FormDataRegistry.registerForm(Forms.PUSH);
//        FormDataRegistry.registerForm(Forms.PULL);
//        FormDataRegistry.registerForm(Forms.RAISE);
//        FormDataRegistry.registerForm(Forms.LOWER);
//        FormDataRegistry.registerForm(Forms.ROTATE);
        // TODO: Add mouse wheel input (once the input module supports it). Mouse wheel 1; forwards/away from the player.
        //  Add mouse wheel input (once the input module supports it). Mouse wheel -1; towards the player/backwards.


        // Motion! 7 different movement options.
        // Left, right, up, down, jump, sneak, sprint.
        LinkedList<InputData> inputs = new LinkedList<>();
        for (Map.Entry<String, Integer> key : KeyboardInputModule.getMovementKeys().entrySet()) {
            inputs.clear();
            // TODO: Add in a mandatory key release condition so you can't activate these by holding down a key.
            //  Also account for the direction modifier.

            KeyInput first, second;
            // Problem: The tick delay is being combined with the key press into a simultaneous condition.
            // Not good.
            first = KeyDataBuilder.createInput(key.getValue(), true, 0);
            // Has to be a relatively quick input.
            second = KeyDataBuilder.createInput(key.getValue(), 0, 15);
            inputs.add(KeyDataBuilder.createChainedInput(first, second));
            FormDataRegistry.registerForm(inputs, Forms.STEP, InputConstants.Type.KEYSYM);
        }
    }
}
