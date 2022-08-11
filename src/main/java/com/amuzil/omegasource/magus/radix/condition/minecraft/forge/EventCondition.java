package com.amuzil.omegasource.magus.radix.condition.minecraft.forge;

import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;
import java.util.function.Function;

public class EventCondition<E extends Event> extends Condition {
	private final Consumer<E> listener;

	public EventCondition(Function<E, Boolean> condition) {
		this.listener = event -> {
			if (condition.apply(event)) {
				this.onSuccess.run();
			} else {
				this.onExpire.run();
			}
		};
	}

	@Override
	public void register(Runnable onSuccess, Runnable onExpire) {
		super.register(onSuccess, onExpire);
		MinecraftForge.EVENT_BUS.addListener(listener);
	}

	@Override
	public void unregister() {
		MinecraftForge.EVENT_BUS.unregister(listener);
	}
}
