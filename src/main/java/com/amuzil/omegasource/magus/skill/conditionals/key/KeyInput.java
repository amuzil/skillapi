package com.amuzil.omegasource.magus.skill.conditionals.key;


import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.mojang.blaze3d.platform.InputConstants;

/**
 *
 * @param key The key.
 * @param release Whether the key should be released after.
 * @param timeout Maximum amount of time this takes before it expires. Rather than using maxDelay, just set the timeout
 *                of the next Condition in the path to be whatever you need.
 * @param held How long the key should be held for.
 */
// Note: For minDelay and maxDelay, adjust an object's condition path.
public record KeyInput(InputConstants.Key key, boolean release, int held, int timeout) implements InputData {}