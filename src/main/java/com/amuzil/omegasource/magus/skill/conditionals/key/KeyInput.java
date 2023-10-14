package com.amuzil.omegasource.magus.skill.conditionals.key;


import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.mojang.blaze3d.platform.InputConstants;

/**
 *
 * @param key The key.
 * @param minDelay Minimum delay that must pass between the current key input and the next.
 * @param maxDelay Has to be at least equal to minDelay. The maximum amount of time that can pass in between
 *                 the current key input, and the next.
 * @param held How long the key should be held for.
 */
public record KeyInput(InputConstants.Key key, int minDelay, int maxDelay, int held) implements InputData {}