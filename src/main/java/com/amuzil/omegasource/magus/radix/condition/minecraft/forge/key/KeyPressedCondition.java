package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedEventCondition;
import com.amuzil.omegasource.magus.skill.util.KeyboardData;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

public class KeyPressedCondition extends TickTimedEventCondition {
    //Upon *any* key (not including modifiers or wasd) being pressed, return true
    public KeyPressedCondition(int timeout) {
        super(TickEvent.Type.CLIENT, TickEvent.Phase.START, new EventCondition<InputEvent.KeyInputEvent>(
                event -> !KeyboardData.ignore(event.getKey())
        ), false, timeout);
    }
}
