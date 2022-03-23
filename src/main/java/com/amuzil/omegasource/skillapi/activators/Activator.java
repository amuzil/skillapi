package com.amuzil.omegasource.skillapi.activators;

/**
 * Covers all the different ways to activate an ability.
 * Gonna use an enum to describe for now. Will update that later; think of it as a placeholder.
 */
public class Activator {

    /**
     * 4 main types of activation. Each of these have sub-types. Again, I'll define them as an enum for now and
     * consolidate for good code practice later.
     */
    public enum ActivatorType {
        RADIAL,
        HOTKEY,
        MULTIKEY,
        GESTURE
    }

    public enum RadialType {
        MOUSE,
        NUMBER
    }

    /**
     * These 2 types are generally used for multikey. Initialise is how to start listening for the hotkey or key combo,
     * and the execution is when to stop.
     */
    public enum InitialiseType {
        TOGGLE,
        HOLD
    }

    public enum ExecuteType {
        DESIGNATED,
        SPECIAL
    }

    /**
     * Covers a range of VR gestures for those who aren't too flexible or athletically inclined, and those
     * who are quite adept in their range of motion.
     */
    public enum GestureType {
        SIMPLE,
        COMPLEX
    }
}
