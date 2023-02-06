package com.amuzil.omegasource.magus.skill.modifiers;

import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.KeyHeldModifierListener;
import com.mojang.blaze3d.platform.InputConstants;

public class ModifiersRegistry {

    public static Modifier FOCUS;

    public static void init() {
        FOCUS = new Modifier(new HeldModifierData(), new KeyHeldModifierListener(InputConstants.getKey("key.mouse.right")));
    }

}
