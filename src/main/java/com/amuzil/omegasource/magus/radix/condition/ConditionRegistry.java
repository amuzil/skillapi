package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.forms.Form;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ConditionRegistry {

    // Id, Condition. Stuff is put into this map when registering.
    private static final HashMap<Integer, Condition> conditions = new HashMap<>();
    private static final Map<Condition, Integer> conditionIDs = new HashMap<>();

    static int id = 0;
    public static void register(Condition condition) {
        if (!conditions.containsValue(condition)) {
            conditions.put(id, condition);
            conditionIDs.put(condition, id);
            id++;
        }
    }

    public static Condition getCondition(int id) {
        return conditions.get(id);
    }

    public static List<Condition> getConditions() {
        return new ArrayList<>(conditions.values());
    }

    public static int getID(Condition condition) {
        return conditionIDs.get(condition);
    }
}
