package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;

/**
 * How we're going to do this:
 * Mouse input is tracked 2D wise. If we need to track rotation, that's a separate thing for 3D.
 *
 * MouseInput, or the base layer, has a tiny snapshot of the player's mouse movement.
 * MousePermutation is a longer input segment, or line.
 * MouseCombination or MouseDrawing is a whole shape.
 * Define a start and stop, or start position, direction, and how long the player has to move their mouse.
 *
 * Get a Bézier curve function working. This tracks input segments, then we path through the input segments.
 */
public record MouseMotionInput(double x, double y) implements InputData {
}
