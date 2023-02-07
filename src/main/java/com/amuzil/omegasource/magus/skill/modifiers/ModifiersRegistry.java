package com.amuzil.omegasource.magus.skill.modifiers;

import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.KeyHeldModifierListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class ModifiersRegistry {

    //used to look up what instance to deserialise into from nbt
    private static Map<String, ModifierData> modifierDataTypes;

    public static Modifier FOCUS;

    public static void init() {
        modifierDataTypes = new HashMap<>();

        ModifierData heldModifierData = new HeldModifierData();
        FOCUS = new Modifier(heldModifierData, new KeyHeldModifierListener(InputConstants.getKey("key.mouse.right")));
        modifierDataTypes.put(heldModifierData.getName(), heldModifierData);
    }

    public static ModifierData fromCompoundTag(CompoundTag compoundTag) {
        ModifierData data = modifierDataTypes.get(compoundTag.getString("dataIdentifier"));
        data.deserializeNBT(compoundTag);
        return data;
    }

}
