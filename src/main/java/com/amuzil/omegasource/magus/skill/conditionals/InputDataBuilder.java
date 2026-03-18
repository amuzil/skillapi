package com.amuzil.omegasource.magus.skill.conditionals;

import com.amuzil.omegasource.magus.skill.conditionals.inputdata.ChainedInputData;
import com.amuzil.omegasource.magus.skill.conditionals.inputdata.MultiInputData;

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

    public static MultiInputData createMultiInput(InputData... inputs) {
        return new MultiInputData(toInputs(inputs));
    }

    //Creates a permutation based off of each individual key input
    public static ChainedInputData createChainedInput(InputData... inputs) {
        LinkedList<MultiInputData> data = new LinkedList<>();
        for (InputData input : inputs)
            data.add(createMultiInput(input));
        return new ChainedInputData(data);
    }

    public static InputData createChainedInput(MultiInputData... inputs) {
        return new ChainedInputData(new LinkedList<>(List.of(inputs)));
    }
}
