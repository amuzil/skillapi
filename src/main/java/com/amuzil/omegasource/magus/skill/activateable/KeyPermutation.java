package com.amuzil.omegasource.magus.skill.activateable;

import java.util.ArrayList;
import java.util.List;

/**
 * Essentially a wrapper class for a part of combination/sequential list of keys.
 * This is meant to be a fragment of a whole key-combination, and it allows developers
 * to break up the combination in a way that's readable + makes sense.
 * ONLY SUPPORTS MULTIPLE KEYS AT ONCE. DELAY IS USELESS HERE. If multiple keys are passed with *different* delay,
 * the key with the most delay will be read.
 */
public class KeyPermutation {

    //Key and delay.
    private List<KeyInput> keys;

    public KeyPermutation() {
        this.keys = new ArrayList<>();
    }

    public KeyPermutation(List<KeyInput> keys) {
        this.keys = keys;
    }

    public KeyPermutation(KeyInput info) {
        this.keys = new ArrayList<>();
        this.keys.add(info);
    }

    public List<KeyInput> getKeys() {
        return this.keys;
    }

}
