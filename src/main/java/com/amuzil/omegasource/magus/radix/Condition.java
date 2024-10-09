package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.registry.Registries;

import java.util.List;

public abstract class Condition {

	public enum Result {
		SUCCESS,
		FAILURE,
		NONE
	}

	protected static final Runnable NO_OPERATION = () -> RadixUtil.getLogger().debug("Result: No Operation");

	protected String name;
	protected boolean active = false;
	protected Runnable onSuccess;
	protected Runnable onFailure;

	// TODO: Change to registerRunnables()
	public void register(String name, Runnable onSuccess, Runnable onFailure) {
		this.name = name;
		//RadixUtil.getLogger().debug("Registering results");
//		if (this instanceof KeyPressCondition && ((KeyPressCondition) this).getKey() == 0)
//			Thread.dumpStack();
		//RadixUtil.getLogger().debug("Result: success");
		this.onSuccess = onSuccess;
		//			RadixUtil.getLogger().debug("Result: failure: " + getClass());
		//			Thread.dumpStack();
		this.onFailure = onFailure;
		registerEntry();
	}

	// TODO: Change this to registerListeners()
	public void register() {}

	// Every Condition needs to call this in their constructor
	public void registerEntry() {
		ConditionRegistry.register(this);
	}

	public void unregister() {
//		Thread.dumpStack();
		RadixUtil.getLogger().debug("Unregistering condition:" + getClass());
		// This should not cause any errors when called if the condition is
		// already unregistered or was never registered in the first place
	}

	protected Runnable runOn(Result result) {
		return switch (result) {
			case SUCCESS -> onSuccess;
			case FAILURE -> onFailure;
			case NONE -> NO_OPERATION;
		};
	}

	public String name() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Change this for custom conditions/conditions you want registered in your own mod
	public String modID() {
		return Magus.MOD_ID;
	}

	public void reset() {}

	public boolean getActiveStatus() {
		return active;
	}

	public static boolean startsWith(List<Condition> conditions, List<Condition> subConditions) {
		try {
			return conditions.subList(0, subConditions.size()).equals(subConditions);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	// TODO - May need to override the equals or create a new method to check if conditions have matching prefixes
//	@Override
//	public boolean equals(Object obj) {
//		return super.equals(obj);
//	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[ " + name + " ]";
	}

	public Runnable onSuccess() {
		return this.onSuccess;
	}

	public Runnable onFailure() {
		return this.onFailure;
	}
}
