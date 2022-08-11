package com.amuzil.omegasource.magus.radix;

public abstract class Condition {
	protected Runnable onSuccess;
	protected Runnable onExpire;

	public void register(Runnable onSuccess, Runnable onExpire) {
		this.onSuccess = onSuccess;
		this.onExpire = onExpire;
	}

	public void unregister() {
		// This should not cause any errors when called if the condition is
		// already unregistered or was never registered in the first place
	}
}
