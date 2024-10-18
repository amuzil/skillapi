package com.amuzil.omegasource.magus.skill.modifiers;

import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.*;
import com.amuzil.omegasource.magus.skill.modifiers.listeners.*;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifiersRegistry {

    //used to look up what instance to deserialize into from nbt
    private static Map<String, Modifier> modifierDataTypes;

    public static Modifier FOCUS;
    public static Modifier MULTI;
    public static Modifier DIRECTION;
    public static Modifier TARGET;
    public static Modifier GESTURE;

    public static Modifier CONTROL;

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

        ModifierData gestureModifierData = new MouseGestureModifierData();
        GESTURE = new Modifier(gestureModifierData, new MouseGestureModifierListener());
        modifierDataTypes.put(gestureModifierData.getName(), GESTURE);

        ModifierData scrollModifierData = new MouseScrollModifierData();
        CONTROL = new Modifier(scrollModifierData, new MouseScrollModifierListener());
        modifierDataTypes.put(scrollModifierData.getName(), CONTROL);

    }

    public static List<Modifier> getModifiers() {
        return modifierDataTypes.values().stream().toList();
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
