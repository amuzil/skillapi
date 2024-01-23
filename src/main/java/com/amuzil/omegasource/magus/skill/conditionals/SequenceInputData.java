package com.amuzil.omegasource.magus.skill.conditionals;

import java.util.List;

/**
 * @param permutations Takes a list of permutations to turn into a generic input list.
 */
public record SequenceInputData(List<MultiInputData> permutations) implements InputData {
}
