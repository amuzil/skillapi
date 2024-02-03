package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import org.lwjgl.glfw.GLFW;

public class KeyReleaseCondition extends TickTimedCondition {
	public KeyReleaseCondition(Key key, int timeout) {
		super(Type.CLIENT, Phase.START, timeout, Result.FAILURE, new EventCondition<>(
				InputEvent.Key.class, event -> event.getKey() == key.getValue() && (event.getAction() == GLFW.GLFW_RELEASE
						|| !((KeyboardMouseInputModule)Magus.keyboardInputModule).keyPressed(key.getValue())
				)
		), Result.SUCCESS, Result.FAILURE);
	}
}
