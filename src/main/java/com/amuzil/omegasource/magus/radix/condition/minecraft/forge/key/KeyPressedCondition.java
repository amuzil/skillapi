package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.amuzil.omegasource.magus.skill.util.data.KeyboardData;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Used to determine whether any key was pressed during a time period. 
 */
public class KeyPressedCondition extends TickTimedCondition {
    //Upon *any* key (not including modifiers or wasd) being pressed, return true
    public KeyPressedCondition(int timeout) {
        super(TickEvent.Type.CLIENT, TickEvent.Phase.START, timeout, Result.SUCCESS, new EventCondition<>(
                InputEvent.Key.class, event -> !KeyboardData.ignore(event.getKey())
        ), Result.SUCCESS, Result.FAILURE);
    }
}
