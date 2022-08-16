package com.amuzil.omegasource.magus.skill.activateable;

import java.util.LinkedList;

/**
 * Stores all possible permutations.
 */
public class KeyCombination {

    private LinkedList<KeyPermutation> keys;

    public KeyCombination() {
        this.keys = new LinkedList<>();
    }

    public KeyCombination(KeyPermutation keyP) {
        this.keys = new LinkedList<>();
        this.keys.add(keyP);
    }

    public KeyCombination (LinkedList<KeyPermutation> keys) {
        this.keys = keys;
    }

    public LinkedList<KeyPermutation> getKeys() {
        return this.keys;
    }

}