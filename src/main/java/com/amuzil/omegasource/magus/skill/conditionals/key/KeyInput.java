package com.amuzil.omegasource.magus.skill.conditionals.key;


import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.Objects;

//Takes a key, delay, and held length.
public final class KeyInput implements InputData {
    private final InputConstants.Key key;
    private final int minDelay;
    private final int maxDelay;
    private final int held;

    public KeyInput(
            InputConstants.Key key,
            int minDelay,
            int maxDelay,
            int held
    ) {
        this.key = key;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.held = held;
    }

    public InputConstants.Key key() {
        return key;
    }

    public int minDelay() {
        return minDelay;
    }

    public int maxDelay() {
        return maxDelay;
    }

    public int held() {
        return held;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (KeyInput) obj;
        return Objects.equals(this.key, that.key) &&
                this.minDelay == that.minDelay &&
                this.maxDelay == that.maxDelay &&
                this.held == that.held;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, minDelay, maxDelay, held);
    }

    @Override
    public String toString() {
        return "KeyInput[" +
                "key=" + key + ", " +
                "minDelay=" + minDelay + ", " +
                "maxDelay=" + maxDelay + ", " +
                "held=" + held + ']';
    }

}