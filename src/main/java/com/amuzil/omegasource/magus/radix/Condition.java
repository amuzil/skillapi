package com.amuzil.omegasource.magus.radix;

public abstract class Condition {
	public enum Result {
		SUCCESS,
		FAILURE,
		NONE
	}

	protected static final Runnable NO_OPERATION = () -> RadixUtil.getLogger().debug("Result: No Operation");

	protected Runnable onSuccess;
	protected Runnable onFailure;

	public void register(Runnable onSuccess, Runnable onFailure) {
		//RadixUtil.getLogger().debug("Registering results");
//		if (this instanceof KeyPressCondition && ((KeyPressCondition) this).getKey() == 0)
//			Thread.dumpStack();
		this.onSuccess = () -> {
			//RadixUtil.getLogger().debug("Result: success");
			onSuccess.run();
		};
		this.onFailure = () -> {
//			RadixUtil.getLogger().debug("Result: failure: " + getClass());
//			Thread.dumpStack();
			onFailure.run();
		};
	}

	public void register() {

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

	// Need to log each condition's id too
	public abstract String name();
}
