package com.amuzil.omegasource.magus.radix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RadixUtil {

	private static final Logger logger = LogManager.getLogger(RadixTree.class);

	public static Logger getLogger() {
		return logger;
	}

	public static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
