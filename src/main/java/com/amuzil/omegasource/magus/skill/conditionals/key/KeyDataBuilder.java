package com.amuzil.omegasource.magus.skill.conditionals.key;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Uses KeyInput, KeyCombination, KeyPermutation.
 * Acts as a factory for those records.
 * Allows for easy conversion between them.
 */
public class KeyDataBuilder {

    public static KeyInput createInput(InputConstants.Key key, int minDelay, int maxDelay, int held) {
        return new KeyInput(key, minDelay, maxDelay, held);
    }

    public static KeyPermutation createPermutation(KeyInput... inputs) {
        LinkedList<KeyInput> data = new LinkedList<>(List.of(inputs));
        return new KeyPermutation(data);
    }

    //Creates a permutation based off of each individual key input
    public static KeyCombination createCombination(KeyInput... inputs) {
        LinkedList<KeyPermutation> data = new LinkedList<>();
        for (KeyInput input : inputs)
            data.add(createPermutation(input));
        return new KeyCombination(data);
    }

    public static KeyCombination createCombination(KeyPermutation... inputs) {
        LinkedList<KeyPermutation> data = new LinkedList<>(List.of(inputs));
        return new KeyCombination(data);
    }
}
