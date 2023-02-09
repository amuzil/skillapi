package com.amuzil.omegasource.magus.skill.modifiers;

import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.KeyHeldModifierListener;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class ModifiersRegistry {

    //used to look up what instance to deserialize into from nbt
    private static Map<String, Modifier> modifierDataTypes;

    public static Modifier FOCUS;

    public static void init() {
        modifierDataTypes = new HashMap<>();

        ModifierData heldModifierData = new HeldModifierData();
        FOCUS = new Modifier(heldModifierData, new KeyHeldModifierListener());
        modifierDataTypes.put(heldModifierData.getName(), FOCUS);
    }

    public static ModifierData fromCompoundTag(CompoundTag compoundTag) {
        ModifierData data = modifierDataTypes.get(compoundTag.getString("dataIdentifier")).data();
        data.deserializeNBT(compoundTag);
        return data;
    }

    public static Modifier fromName(String name) {
        return modifierDataTypes.get(name);
    }
}
