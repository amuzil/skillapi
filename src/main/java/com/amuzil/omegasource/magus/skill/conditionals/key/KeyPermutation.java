package com.amuzil.omegasource.magus.skill.conditionals.key;

import java.util.LinkedList;

/**
 * Essentially a wrapper class for a part of combination/sequential list of keys.
 * This is meant to be a fragment of a whole key-combination, and it allows developers
 * to break up the combination in a way that's readable + makes sense.
 * ONLY SUPPORTS MULTIPLE KEYS AT ONCE. DELAY IS USELESS HERE. If multiple keys are passed with *different* delay,
 * the key with the most delay will be read.
 */
public record KeyPermutation (
        LinkedList<KeyInput> keys
) {
}
