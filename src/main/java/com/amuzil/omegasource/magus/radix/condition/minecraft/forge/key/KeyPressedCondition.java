package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.amuzil.omegasource.magus.skill.util.data.KeyboardData;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

import java.util.function.Function;

/**
 * Used to determine whether any key was pressed during a time period. 
 */
public class KeyPressedCondition extends TickTimedCondition {
    //Upon *any* key (not including modifiers or wasd) being pressed, return true
    public KeyPressedCondition(int timeout, Function<InputEvent.Key, Boolean> predicate) {
        super(TickEvent.Type.CLIENT, TickEvent.Phase.START, timeout, Result.SUCCESS, new EventCondition<>(
                InputEvent.Key.class, predicate), Result.SUCCESS, Result.FAILURE);
    }

    @Override
    public String name() {
        return "key_pressed";
    }
}
