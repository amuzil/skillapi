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

    /* Integers */
    public static KeyInput createInput(int key, int held) {
        return new KeyInput(InputConstants.getKey(key, -1), false,  held, -1);
    }

    public static KeyInput createInput(int key, int held, int timeout) {
        return new KeyInput(InputConstants.getKey(key, -1), false, held, timeout);
    }

    public static KeyInput createInput(int key, boolean released, int held) {
        return new KeyInput(InputConstants.getKey(key, -1), released, held, -1);
    }

    public static KeyInput createInput(int key, boolean released, int held, int timeout) {
        return new KeyInput(InputConstants.getKey(key, -1), released, held, timeout);
    }

    /* Strings */
    public static KeyInput createInput(String key, int held) {
        return new KeyInput(InputConstants.getKey(key), false, held, -1);
    }

    public static KeyInput createInput(String key, boolean released, int held) {
        return new KeyInput(InputConstants.getKey(key), released, held, -1);
    }

    public static KeyInput createInput(String key, boolean released, int held, int timeout) {
        return new KeyInput(InputConstants.getKey(key), released, held, timeout);
    }

    public static KeyInput createInput(String key,  int held, int timeout) {
        return new KeyInput(InputConstants.getKey(key), false, held, timeout);
    }

    /* Input Constants */
    public static KeyInput createInput(InputConstants.Key key, boolean released,int held) {
        return new KeyInput(key, released, held, -1);
    }

    public static KeyInput createInput(InputConstants.Key key, int held) {
        return new KeyInput(key, false, held, -1);
    }

    public static KeyInput createInput(InputConstants.Key key, int held, int timeout) {
        return new KeyInput(key, false, held, timeout);
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
