package com.amuzil.omegasource.magus.radix;

public interface Leaf<T> {
	void burn();

	void reset();

	T measure();
}
