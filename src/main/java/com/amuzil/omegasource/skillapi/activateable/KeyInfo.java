package com.amuzil.omegasource.skillapi.activateable;

import com.mojang.blaze3d.platform.InputConstants;
import com.sun.jna.platform.KeyboardUtils;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.player.KeyboardInput;

import java.awt.event.KeyEvent;

//Takes a key, delay, and held length.
public class KeyInfo {

    private InputConstants.Key key;
    //In ticks (20 ticks a second). Can be used to make someone wait between key presses for something.
    private int minDelay;
    //Maximum delay before the listener should stop caring
    private int maxDelay;
    //Also in ticks. -1 to 1 all are effectively just pressed. Remember, this class doesn't check
    //the logic, only provides the values for which to apply it with.
    private int held;


    public KeyInfo() {
        this.key = InputConstants.UNKNOWN;
        this.minDelay = 0;
        this.held = -1;
    }

    public KeyInfo(InputConstants.Key key) {
        this.key = key;
        this.minDelay = 0;
        this.held = - 1;
    }

    public KeyInfo(InputConstants.Key key, int delay) {
        this.key = key;
        this.minDelay = delay;
        this.held = -1;
    }

    public KeyInfo(InputConstants.Key key, int delay, int held) {
        this.key = key;
        this.minDelay = delay;
        this.held = held;
    }

    public InputConstants.Key getKey() {
        return this.key;
    }

    public int getMinDelay() {
        return this.minDelay;
    }

    public int getHeld() {
        return this.held;
    }
}
