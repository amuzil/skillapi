package com.amuzil.omegasource.magus.skill.conditionals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Designed as a factory class for taking InputDat and turning it into a linked list
 * for InputModule use.
 */
public class InputDataBuilder {

    public static LinkedList<InputData> toInputs(InputData... data) {
        return new LinkedList<>(List.of(data));
    }

    public static InputPermutation createPermutation(InputData... inputs) {
        return new InputPermutation(toInputs(inputs));
    }

    //Creates a permutation based off of each individual key input
    public static InputCombination createCombination(InputData... inputs) {
        LinkedList<InputPermutation> data = new LinkedList<>();
        for (InputData input : inputs)
            data.add(createPermutation(input));
        return new InputCombination(data);
    }

    public static InputData createCombination(InputPermutation... inputs) {
        return new InputCombination(new LinkedList<>(List.of(inputs)));
    }
}
