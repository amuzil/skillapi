package com.amuzil.omegasource.magus.skill.conditionals;

import java.util.List;

/**
 * @param inputs Generic list of InputData to turn into InputPermutations. Good for containing inputs of multiple things
 *               that happen simultaneously.
 */
public record InputPermutation(List<InputData> inputs) implements InputData {
}
