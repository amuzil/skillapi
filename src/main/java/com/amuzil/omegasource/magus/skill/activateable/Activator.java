package com.amuzil.omegasource.magus.skill.activateable;

import com.amuzil.omegasource.magus.radix.Condition;

import java.util.LinkedList;

public class Activator {
    //Returns a linked list of all conditions derived from the current class
    public LinkedList<Condition> toCondition() {
        return new LinkedList<>();
    }
}
