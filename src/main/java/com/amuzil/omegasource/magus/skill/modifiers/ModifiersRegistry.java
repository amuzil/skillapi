package com.amuzil.omegasource.magus.skill.modifiers;

import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.DirectionModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.MultiModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.TargetModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.DirectionModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.KeyHeldModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.TargetModifierListener;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class ModifiersRegistry {

    //used to look up what instance to deserialize into from nbt
    private static Map<String, Modifier> modifierDataTypes;

    public static Modifier FOCUS;
    public static Modifier MULTI;
    public static Modifier DIRECTION;
    public static Modifier TARGET;

    public static void init() {
        modifierDataTypes = new HashMap<>();

        ModifierData heldModifierData = new HeldModifierData();
        FOCUS = new Modifier(heldModifierData, new KeyHeldModifierListener());
        modifierDataTypes.put(heldModifierData.getName(), FOCUS);

        ModifierData multiModifierData = new MultiModifierData();
        MULTI = new Modifier(multiModifierData, null);
        modifierDataTypes.put(multiModifierData.getName(), MULTI);


        ModifierData directionModifierData = new DirectionModifierData();
        DIRECTION = new Modifier(directionModifierData, new DirectionModifierListener());
        modifierDataTypes.put(directionModifierData.getName(), DIRECTION);


        ModifierData targetModifierData = new TargetModifierData();
        TARGET = new Modifier(targetModifierData, new TargetModifierListener());
        modifierDataTypes.put(targetModifierData.getName(), TARGET);
    }

    public static ModifierData fromCompoundTag(CompoundTag compoundTag) {
        ModifierData data = modifierDataTypes.get(compoundTag.getString("dataIdentifier")).copy().data();
        data.deserializeNBT(compoundTag);
        return data;
    }

    public static Modifier fromName(String name) {
        return modifierDataTypes.get(name).copy();
    }
}
