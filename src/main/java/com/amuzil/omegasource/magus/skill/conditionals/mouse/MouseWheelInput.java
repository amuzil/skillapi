package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

/**
 *
 * @param direction Which way the mouse wheel should go. If you want to just press the mouse wheel, use KeyInput.
 * @param totalChange The total mouse delta amount to wait for (e.g 10 means scrolling forwards a lot).
 * @param duration How long the mouse wheel should be held in a certain direction.
 */
public record MouseWheelInput(MouseDataBuilder.Direction direction, float totalChange, int duration, int timeout) implements InputData {
}
