package com.amuzil.omegasource.magus.skill.activateable.key;

import com.amuzil.omegasource.magus.skill.activateable.Activator;
import com.mojang.blaze3d.platform.InputConstants;

//Takes a key, delay, and held length.
public record KeyInput (
        InputConstants.Key key,
        int minDelay,
        int maxDelay,
        int held
) {
}