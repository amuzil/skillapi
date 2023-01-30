package com.amuzil.omegasource.magus.skill.conditionals.key;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Essentially a wrapper class for a part of combination/sequential list of keys.
 * This is meant to be a fragment of a whole key-combination, and it allows developers
 * to break up the combination in a way that's readable + makes sense.
 * ONLY SUPPORTS MULTIPLE KEYS AT ONCE. DELAY IS USELESS HERE. If multiple keys are passed with *different* delay,
 * the key with the most delay will be read.
 */
public final class KeyPermutation implements InputData {
    private final LinkedList<KeyInput> keys;

    /**
     *
     */
    public KeyPermutation(
            LinkedList<KeyInput> keys
    ) {
        this.keys = keys;
    }

    public LinkedList<KeyInput> keys() {
        return keys;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (KeyPermutation) obj;
        return Objects.equals(this.keys, that.keys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys);
    }

    @Override
    public String toString() {
        return "KeyPermutation[" +
                "keys=" + keys + ']';
    }

}
