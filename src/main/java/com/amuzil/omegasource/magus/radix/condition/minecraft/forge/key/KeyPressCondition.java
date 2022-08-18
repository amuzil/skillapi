package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import org.lwjgl.glfw.GLFW;

public class KeyPressCondition extends TickTimedCondition {
	public KeyPressCondition(Key key, int timeout) {
		super(Type.CLIENT, Phase.START, timeout, Result.FAILURE, new EventCondition<InputEvent.Key>(
			event -> event.getAction() == GLFW.GLFW_PRESS && event.getKey() == key.getValue()
		), Result.SUCCESS, Result.FAILURE);
	}
}
