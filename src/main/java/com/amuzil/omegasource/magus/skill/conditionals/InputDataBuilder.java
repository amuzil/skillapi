package com.amuzil.omegasource.magus.skill.conditionals;

import java.util.LinkedList;
import java.util.List;

/**
 * Designed as a factory class for taking InputData and turning it into a linked list
 * for InputModule use.
 */
public class InputDataBuilder {

    public static LinkedList<InputData> toInputs(InputData... data) {
        return new LinkedList<>(List.of(data));
    }

    public static MultiInputData createPermutation(InputData... inputs) {
        return new MultiInputData(toInputs(inputs));
    }

    //Creates a permutation based off of each individual key input
    public static SequenceInputData createCombination(InputData... inputs) {
        LinkedList<MultiInputData> data = new LinkedList<>();
        for (InputData input : inputs)
            data.add(createPermutation(input));
        return new SequenceInputData(data);
    }

    public static InputData createCombination(MultiInputData... inputs) {
        return new SequenceInputData(new LinkedList<>(List.of(inputs)));
    }
}
