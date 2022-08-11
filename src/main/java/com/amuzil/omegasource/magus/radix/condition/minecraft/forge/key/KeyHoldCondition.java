package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key;

import com.amuzil.omegasource.magus.radix.Condition;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class KeyHoldCondition extends Condition {
	private final Consumer<KeyInputEvent> keyInputListener;
	private final Consumer<ClientTickEvent> clientTickListener;

	private int currentTotal;
	private boolean isHolding;
	private int currentHolding;

	public KeyHoldCondition(Key key, int duration, int timeout) {
		assert duration >= 0; // TODO Assert util
		assert timeout >= 0; // TODO Assert util

		this.currentTotal = 0;
		this.isHolding = false;
		this.currentHolding = 0;

		this.keyInputListener = event -> {
			if (event.getKey() == key.getValue()) {
				if (event.getAction() == GLFW.GLFW_PRESS) {
					this.isHolding = true;
					this.currentHolding = 0;
				} else if (event.getAction() == GLFW.GLFW_RELEASE) {
					this.isHolding = false;
				}
			} else {
				this.onExpire.run();
			}
		};

		this.clientTickListener = event -> {
			if (event.phase == ClientTickEvent.Phase.START) {
				this.currentTotal += 1;
				if (this.isHolding) {
					this.currentHolding += 1;
					if (this.currentHolding >= duration) {
						this.onSuccess.run();
					}
				}
				if (this.currentTotal >= timeout) {
					this.onExpire.run();
				}
			}
		};
	}

	@Override
	public void register(Runnable onSuccess, Runnable onExpire) {
		super.register(onSuccess, onExpire);
		MinecraftForge.EVENT_BUS.addListener(keyInputListener);
		MinecraftForge.EVENT_BUS.addListener(clientTickListener);
	}

	@Override
	public void unregister() {
		MinecraftForge.EVENT_BUS.unregister(keyInputListener);
		MinecraftForge.EVENT_BUS.unregister(clientTickListener);
	}
}
