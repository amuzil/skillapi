package com.amuzil.omegasource.magus.radix.condition.util;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedEventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressCondition;
import com.amuzil.omegasource.magus.skill.activateable.KeyCombination;
import com.amuzil.omegasource.magus.skill.activateable.KeyInfo;
import com.amuzil.omegasource.magus.skill.activateable.KeyPermutation;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

import java.util.LinkedList;

/**
 * Takes data and converts it into a corresponding data structure of conditions.
 * Still has to be turned into tree.
 */
public class ConditionConverter {

    public static LinkedList<Condition> keysToConditions(KeyCombination keys) {
        LinkedList<Condition> ls = new LinkedList<>();
        for (KeyPermutation keyP : keys.getKeys())
            ls.addAll(keysToConditions(keyP));
        return ls;
    }

    public static LinkedList<Condition> keysToConditions(KeyPermutation keys) {
        LinkedList<Condition> ls = new LinkedList<>();
        for (KeyInfo key : keys.getKeys())
            ls.addAll(keyToConditions(key));
        return ls;
    }

    public static LinkedList<Condition> keyToConditions(KeyInfo key) {
        LinkedList<Condition> ls = new LinkedList<>();

        //Any time less than this is just a key press.
        if (key.getHeld() > 3)
            ls.add(new KeyHoldCondition(key.getKey(), key.getHeld(), 50));
        else ls.add(new KeyPressCondition(key.getKey(), 50));

        if (key.getMinDelay() > 0)
            //TODO: Fix this to account for "action keys".
            ls.add(new TickTimedEventCondition(TickEvent.Type.CLIENT, TickEvent.Phase.START,
                    new EventCondition<InputEvent.KeyInputEvent>(
                            event -> false), false, key.getMinDelay()));

        return ls;
    }
}
