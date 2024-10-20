package com.amuzil.omegasource.magus.skill.conditionals.key;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.LinkedList;

/**
 * Stores all possible permutations.
 */
public record ChainedKeyInput(LinkedList<MultiKeyInput> keys) implements InputData {

    public MultiKeyInput first() {
        return keys.get(0);
    }

    public MultiKeyInput last() {
        return keys.get(keys.size() - 1);
    }

    // First KeyInput in the record.
    public KeyInput trueFirst() {
        return first().first();
    }

    // Last KeyInput in the record.
    public KeyInput trueLast() {
        return last().last();
    }
}
