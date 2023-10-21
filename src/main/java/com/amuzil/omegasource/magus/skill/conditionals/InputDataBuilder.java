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
        LinkedList<InputData> out = new LinkedList<>();
        Collections.addAll(out, data);
        return out;
    }

    public static InputPermutation createPermutation(InputData... inputs) {
        LinkedList<InputData> data = new LinkedList<>(List.of(inputs));
        return new InputPermutation(data);
    }

    //Creates a permutation based off of each individual key input
    public static InputCombination createCombination(InputData... inputs) {
        LinkedList<InputPermutation> data = new LinkedList<>();
        for (InputData input : inputs)
            data.add(createPermutation(input));
        return new InputCombination(data);
    }

    public static InputData createCombination(InputPermutation... inputs) {
        LinkedList<InputPermutation> data = new LinkedList<>(List.of(inputs));
        return new InputCombination(data);
    }
}
