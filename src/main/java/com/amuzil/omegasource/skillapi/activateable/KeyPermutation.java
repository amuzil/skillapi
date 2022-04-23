package com.amuzil.omegasource.skillapi.activateable;

import java.util.ArrayList;
import java.util.List;

/**
 * Essentially a wrapper class for a part of combination/sequential list of keys.
 * This is meant to be a fragment of a whole key-combination, and it allows developers
 * to break up the combination in a way that's readable + makes sense.
 */
public class KeyPermutation {

    //Key and delay.
    private List<KeyInfo> keys;

    public KeyPermutation() {
        this.keys = new ArrayList<>();
    }

    public KeyPermutation(List<KeyInfo> keys) {
        this.keys = new ArrayList<>();
        this.keys.addAll(keys);
    }

    public KeyPermutation(KeyInfo info) {
        this.keys = new ArrayList<>();
        this.keys.add(info);
    }

}
