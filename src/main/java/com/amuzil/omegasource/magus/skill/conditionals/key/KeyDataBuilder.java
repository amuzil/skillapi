package com.amuzil.omegasource.magus.skill.conditionals.key;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.LinkedList;
import java.util.List;

/**
 * Uses KeyInput, KeyCombination, KeyPermutation.
 * Acts as a factory for those records.
 * Allows for easy conversion between them.
 */
public class KeyDataBuilder {

    /* Look at #InputConstants for key names, codes, and types. */

    public static KeyInput createInput(int key, int minDelay, int maxDelay, int held) {
        return new KeyInput(InputConstants.getKey(key, -1), minDelay, maxDelay, held);
    }

    public static KeyInput createInput(String key, int minDelay, int maxDelay, int held) {
        return new KeyInput(InputConstants.getKey(key), minDelay, maxDelay, held);
    }

    public static KeyInput createInput(InputConstants.Key key, int minDelay, int maxDelay, int held) {
        return new KeyInput(key, minDelay, maxDelay, held);
    }


    //TODO: Remove these methods due to InputDataBuilder?

    public static MultiKeyInput createPermutation(KeyInput... inputs) {
        LinkedList<KeyInput> data = new LinkedList<>(List.of(inputs));
        return new MultiKeyInput(data);
    }

    //Creates a permutation based off of each individual key input
    public static ChainedKeyInput createCombination(KeyInput... inputs) {
        LinkedList<MultiKeyInput> data = new LinkedList<>();
        for (KeyInput input : inputs)
            data.add(createPermutation(input));
        return new ChainedKeyInput(data);
    }

    public static ChainedKeyInput createCombination(MultiKeyInput... inputs) {
        LinkedList<MultiKeyInput> data = new LinkedList<>(List.of(inputs));
        return new ChainedKeyInput(data);
    }
}
