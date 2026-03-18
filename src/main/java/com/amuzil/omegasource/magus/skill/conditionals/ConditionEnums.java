package com.amuzil.omegasource.magus.skill.conditionals;

public class ConditionEnums {


    /**
     * Covers a range of VR gestures for those who aren't too flexible or athletically inclined, and those
     * who are quite adept in their range of motion.
     */
    public enum GestureType {
        SIMPLE,
        COMPLEX
    }

    /**
     * These 2 types are generally used for multikey. Initialise is how to start listening for the hotkey or key combo,
     * and the execution is when to stop.
     */
    public enum InitialiseType {
        TOGGLE,
        HOLD
    }

    public enum RadialType {
        MOUSE,
        NUMBER
    }

    public enum ExecuteType {
        DESIGNATED,
        SPECIAL
    }
}
