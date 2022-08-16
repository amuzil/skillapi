package com.amuzil.omegasource.magus.skill.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyBindingMap;
import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.blaze3d.platform.InputConstants.*;

public class KeyboardData {
    //Converts InputConstants key data into GLFW

    //TODO: Configurable list of invalid keys
    public static boolean ignore(int keyCode) {
        List<Integer> invalidKeys = new ArrayList<>();
        invalidKeys.add(KEY_A);
        invalidKeys.add(KEY_S);
        invalidKeys.add(KEY_W);
        invalidKeys.add(KEY_D);
        invalidKeys.add(KEY_SPACE);
        invalidKeys.add(GLFW.GLFW_KEY_LEFT_ALT);
        invalidKeys.add(GLFW.GLFW_KEY_RIGHT_ALT);
        invalidKeys.add(GLFW.GLFW_KEY_LEFT_CONTROL);

        return invalidKeys.contains(keyCode);
    }

}
