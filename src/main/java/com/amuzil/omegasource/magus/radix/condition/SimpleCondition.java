package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;

import java.util.function.Supplier;

public class SimpleCondition extends Condition {
	Supplier<Boolean> condition;

	public SimpleCondition(Supplier<Boolean> condition) {
		this.condition = condition;
	}

	@Override
	public void register(Runnable onSuccess, Runnable onExpire) {
		if (condition.get()) {
			onSuccess.run();
		} else {
			onExpire.run();
		}
	}
}
