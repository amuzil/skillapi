package com.amuzil.omegasource.skillapi.activators;

import com.mojang.blaze3d.platform.InputConstants;

//Takes a key, delay, and held length.
public class KeyInfo {

    private InputConstants.Key key;
    //In ticks (20 ticks a second)
    private int delay;
    //Also in ticks. -1 to 1 all are effectively just pressed. Remember, this class doesn't check
    //the logic, only provides the values for which to apply it with.
    private int held;

    public KeyInfo() {
        this.key = InputConstants.UNKNOWN;
        this.delay = 0;
        this.held = -1;
    }

    public KeyInfo(InputConstants.Key key) {
        this.key = key;
        this.delay = 0;
        this.held = - 1;
    }

    public KeyInfo(InputConstants.Key key, int delay) {
        this.key = key;
        this.delay = delay;
        this.held = -1;
    }

    public KeyInfo(InputConstants.Key key, int delay, int held) {
        this.key = key;
        this.delay = delay;
        this.held = -1;
    }

    public InputConstants.Key getKey() {
        return this.key;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getHeld() {
        return this.held;
    }
}
