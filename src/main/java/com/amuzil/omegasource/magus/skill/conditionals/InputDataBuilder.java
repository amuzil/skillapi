package com.amuzil.omegasource.magus.skill.conditionals;

import java.util.LinkedList;
import java.util.List;

/**
 * Designed as a factory class for taking InputDat and turning it into a linked list
 * for InputModule use.
 */
public class InputDataBuilder {

    public static LinkedList<InputData> toInputs(InputData... data) {
        return (LinkedList<InputData>) List.of(data);
    }
}
