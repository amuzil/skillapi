package com.amuzil.omegasource.magus.skill.activateable.key;

import java.util.LinkedList;

/**
 * Stores all possible permutations.
 */
public record KeyCombination (
        LinkedList<KeyPermutation> keys
) {
}
