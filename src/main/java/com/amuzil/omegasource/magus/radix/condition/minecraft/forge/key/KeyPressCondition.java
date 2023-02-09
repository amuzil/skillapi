package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import org.lwjgl.glfw.GLFW;

public class KeyPressCondition extends TickTimedCondition {
	public InputConstants.Key key;

	public KeyPressCondition(Key key, int timeout) {
		super(Type.CLIENT, Phase.START, timeout, Result.FAILURE, InputModule.keyToCondition(key,
				GLFW.GLFW_PRESS), Result.SUCCESS, Result.FAILURE);
		this.key = key;
	}
}
