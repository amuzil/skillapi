package com.amuzil.omegasource.magus.radix.condition.minecraft.forge;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public class TickTimedCondition extends Condition {
	private final Consumer<TickEvent> listener;

	private final Condition subCondition;
	private final Result onSubSuccess;
	private final Result onSubFailure;

	private int current;

	public TickTimedCondition(Type type, Phase phase, int timeout, Result onTimeout, Condition subCondition, Result onSubSuccess, Result onSubFailure) {
		RadixUtil.assertTrue(timeout >= 0, "timeout must be >= 0");
		RadixUtil.assertTrue(onTimeout != Result.NONE, "onTimeout must be != NONE");

		this.subCondition = subCondition;
		this.onSubSuccess = onSubSuccess;
		this.onSubFailure = onSubFailure;

		this.current = 0;

		this.listener = event -> {
			if (event.type == type && event.phase == phase) {
				this.current++;
				if (this.current >= timeout) {
					runOn(onTimeout).run();
					//Test:
					System.out.println("Success?");
				}
			}
		};
	}

	@Override
	public void register(Runnable onSuccess, Runnable onFailure) {
		super.register(onSuccess, onFailure);
		// TODO is this the correct approach?
		this.subCondition.register(runOn(this.onSubSuccess), runOn(this.onSubFailure));
		//Ensures no cast errors occur (blame forge)
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.class, listener);
	}

	@Override
	public void unregister() {
		MinecraftForge.EVENT_BUS.unregister(listener);
		subCondition.unregister();
	}
}
