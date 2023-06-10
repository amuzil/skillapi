package com.amuzil.omegasource.magus.skill.conditionals;

import java.util.Collections;
import java.util.LinkedList;

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
}
