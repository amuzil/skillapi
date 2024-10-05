package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardInputModule;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.RadixUtil;
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
import java.util.List;
import java.util.Map;

public class AvatarFormRegistry {

    public static void registerForms() {
        KeyInput left = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyAttack.getKey(), 0);
        KeyInput right = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyUse.getKey(), 0);
        KeyInput initializer = KeyDataBuilder.createInput("key.keyboard.left.alt", 0);
        KeyInput held = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyShift.getKey(), 20);
        KeyInput sneak = KeyDataBuilder.createInput(Minecraft.getInstance().options.keyShift.getKey(), 16);
        MouseWheelInput forwards = MouseDataBuilder.createWheelInput(MouseDataBuilder.Direction.FORWARDS, 1);
        MouseWheelInput back = MouseDataBuilder.createWheelInput(MouseDataBuilder.Direction.BACK, 1);


        // TODO: Rather than having "push", "pull", "raise", and "lower" as Forms,
        //  make them Effects using the Force Form, Direction modifier & Target Modifier

        /* Mouse Button Forms */
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(left), Forms.STRIKE, RadixTree.InputType.MOUSE);
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(right), Forms.FORCE, RadixTree.InputType.MOUSE);
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(KeyDataBuilder.createMultiInput(left, right)), Forms.BURST, RadixTree.InputType.MOUSE);

        /* Mouse Motion Forms */
        // TODO: Figure out a way to pass a condition that specifies when to track data for this Form and other mouse motion Forms.

        // Forms.SLASH; Sharp mouse motion in a direction. Used for things like air slash.
        // Forms.TWIST; Circular mouse motion in front of the player.
        // Forms.RAISE; Slower mouse motion up. Good for bending material manipulation, especially for Earth.
        // Forms.LOWER; Slower mouse motion down.

        /* Keyboard Forms */
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(initializer), Forms.ARC, RadixTree.InputType.KEYBOARD);
        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak), Forms.BREATHE, RadixTree.InputType.KEYBOARD);
        // Forms.SPIN; Clockwise or anticlockwise cardinal directions. (WDSA or WASD).

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

            KeyInput first, second;

            // key.right is 68
            first = KeyDataBuilder.createInput(key.getValue(), true, 0);
            // Has to be a relatively quick input.
            second = KeyDataBuilder.createInput(key.getValue(), 0, 15);
            inputs.add(first);
            inputs.add(second);
            FormDataRegistry.registerForm(inputs, Forms.STEP, RadixTree.InputType.KEYBOARD);
        }

        /* Combination Forms */
        // Forms.COMPRESS; Shift and mouse wheel maybe?
        // Forms.EXPAND; Shift and mouse wheel in other direction.
        //        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak, forwards), Forms.COMPRESS);
//        FormDataRegistry.registerForm(InputDataBuilder.toInputs(sneak, back), Forms.EXPAND);
    }
}
