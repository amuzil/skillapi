package com.amuzil.omegasource.magus.skill.conditionals.key;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Stores all possible permutations.
 */
public final class KeyCombination implements InputData {
    private final LinkedList<KeyPermutation> keys;

    /**
     *
     */
    public KeyCombination(
            LinkedList<KeyPermutation> keys
    ) {
        this.keys = keys;
    }

    public LinkedList<KeyPermutation> keys() {
        return keys;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (KeyCombination) obj;
        return Objects.equals(this.keys, that.keys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys);
    }

    @Override
    public String toString() {
        return "KeyCombination[" +
                "keys=" + keys + ']';
    }

}
