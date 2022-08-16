package com.amuzil.omegasource.magus.radix.condition.minecraft.forge;

import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;

import java.util.function.Consumer;

public class TickTimedEventCondition extends Condition {
	private final Consumer<TickEvent> listener;

	private final Condition subCondition;
	private final boolean respectSubExpire;

	//TickEvent extends this, so this needs to be protected vs private
	protected int current;

	public TickTimedEventCondition(Type type, Phase phase, Condition subCondition, boolean respectSubExpire, int timeout) {
		assert timeout >= 0; // TODO Assert util

		this.subCondition = subCondition;
		this.respectSubExpire = respectSubExpire;

		this.current = 0;

		this.listener = event -> {
			if (event.type == type && event.phase == phase) {
				this.current += 1;
				if (this.current >= timeout) {
					this.onExpire.run();
				}
			}
		};
	}

	@Override
	public void register(Runnable onSuccess, Runnable onExpire) {
		super.register(onSuccess, onExpire);
		// TODO is this the correct approach?
		this.subCondition.register(onSuccess, this.respectSubExpire ? onExpire : () -> {});
		MinecraftForge.EVENT_BUS.addListener(listener);
	}

	@Override
	public void unregister() {
		MinecraftForge.EVENT_BUS.unregister(listener);
		subCondition.unregister();
	}
}
