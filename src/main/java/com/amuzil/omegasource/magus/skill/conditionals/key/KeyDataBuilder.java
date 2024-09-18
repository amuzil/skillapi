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
        return new KeyInput(InputConstants.getKey(key, -1), false, minDelay, maxDelay, held, -1);
    }

    public static KeyInput createInput(int key, boolean released, int minDelay, int maxDelay, int held) {
        return new KeyInput(InputConstants.getKey(key, -1), released, minDelay, maxDelay, held, -1);
    }

    public static KeyInput createInput(String key, int minDelay, int maxDelay, int held) {
        return new KeyInput(InputConstants.getKey(key), false, minDelay, maxDelay, held, -1);
    }

    public static KeyInput createInput(String key, boolean released, int minDelay, int maxDelay, int held) {
        return new KeyInput(InputConstants.getKey(key), released, minDelay, maxDelay, held, -1);
    }
    public static KeyInput createInput(InputConstants.Key key, boolean released, int minDelay, int maxDelay, int held) {
        return new KeyInput(key, released, minDelay, maxDelay, held, -1);
    }

    public static KeyInput createInput(InputConstants.Key key, int minDelay, int maxDelay, int held) {
        return new KeyInput(key, false, minDelay, maxDelay, held, -1);
    }

    public static KeyInput createInput(InputConstants.Key key, int minDelay, int maxDelay, int held, int timeout) {
        return new KeyInput(key, false, minDelay, maxDelay, held, timeout);
    }

    //TODO: Remove these methods due to InputDataBuilder?

    public static MultiKeyInput createMultiInput(KeyInput... inputs) {
        LinkedList<KeyInput> data = new LinkedList<>(List.of(inputs));
        return new MultiKeyInput(data);
    }

    //Creates a permutation based off of each individual key input
    public static ChainedKeyInput createChainedInput(KeyInput... inputs) {
        LinkedList<MultiKeyInput> data = new LinkedList<>();
        for (KeyInput input : inputs)
            data.add(createMultiInput(input));
        return new ChainedKeyInput(data);
    }

    public static ChainedKeyInput createChainedInput(MultiKeyInput... inputs) {
        LinkedList<MultiKeyInput> data = new LinkedList<>(List.of(inputs));
        return new ChainedKeyInput(data);
    }
}
