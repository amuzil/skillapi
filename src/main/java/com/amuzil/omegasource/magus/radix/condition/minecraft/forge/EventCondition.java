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
			try {
				if (condition.apply(event)) {
					this.onSuccess.run();
				} else {
					this.onFailure.run();
				}
			}
			catch (ClassCastException e) {
				e.printStackTrace();
			}
		};
	}

	@Override
	public void register(Runnable onSuccess, Runnable onFailure) {
		super.register(onSuccess, onFailure);
		MinecraftForge.EVENT_BUS.addListener(listener);
	}

	@Override
	public void unregister() {
		MinecraftForge.EVENT_BUS.unregister(listener);
	}
}
