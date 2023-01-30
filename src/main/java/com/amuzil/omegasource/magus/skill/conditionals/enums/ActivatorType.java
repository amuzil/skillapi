package com.amuzil.omegasource.magus.skill.conditionals.enums;

/**
 * Simple activators take direct events/information to pass to a listener (radial menu and such).
 * Complex take other activators and simple events.
 */
public enum ActivatorType {
    SIMPLE,
    COMPLEX
}