package com.amuzil.omegasource.magus.radix;

public abstract class Condition {
	public enum Result {
		SUCCESS,
		FAILURE,
		NONE
	}

	protected static final Runnable NO_OPERATION = () -> RadixUtil.getLogger().debug("Result: no operation");

	protected Runnable onSuccess;
	protected Runnable onFailure;

	public void register(Runnable onSuccess, Runnable onFailure) {
		RadixUtil.getLogger().debug("Registering results");
		this.onSuccess = () -> {
			//RadixUtil.getLogger().debug("Result: success");
			onSuccess.run();
		};
		this.onFailure = () -> {
			//RadixUtil.getLogger().debug("Result: failure");
			onFailure.run();
		};
	}

	public void unregister() {
		RadixUtil.getLogger().debug("Unregistering results");
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
}
