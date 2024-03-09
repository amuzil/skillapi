package com.amuzil.omegasource.magus.skill.conditionals.inputdata;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.List;

/**
 * @param permutations Takes a list of permutations to turn into a generic input list.
 */
public record ChainedInputData(List<MultiInputData> permutations) implements InputData {
}
