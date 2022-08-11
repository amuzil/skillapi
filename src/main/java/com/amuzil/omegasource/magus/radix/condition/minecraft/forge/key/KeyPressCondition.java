package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedEventCondition;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import org.lwjgl.glfw.GLFW;

public class KeyPressCondition extends TickTimedEventCondition {
	public KeyPressCondition(Key key, int timeout) {
		super(Type.CLIENT, Phase.START, new EventCondition<KeyInputEvent>(
			event -> event.getAction() == GLFW.GLFW_PRESS && event.getKey() == key.getValue()
		), false, timeout);
	}
}
