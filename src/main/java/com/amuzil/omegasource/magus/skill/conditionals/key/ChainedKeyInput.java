package com.amuzil.omegasource.magus.skill.conditionals.key;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.LinkedList;

/**
 * Stores all possible permutations.
 */
public record ChainedKeyInput(LinkedList<MultiKeyInput> keys) implements InputData {
}