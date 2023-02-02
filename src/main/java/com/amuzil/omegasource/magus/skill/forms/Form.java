package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.LinkedList;

public class Form {
    private String name;
    private LinkedList<InputData> inputData;

    public Form(String name, LinkedList<InputData> inputData) {
        this.name = name;
        this.inputData = inputData;
        Registries.registerForm(this);
    }

    /**
     * Just in case. I don't think we care about the MOD_ID as forge handles that.
     * @param name
     * @param modID
     */
    public Form(String name, LinkedList<InputData> inputData, String modID) {
        this(name, inputData);
    }

    public String name() {
        return name;
    }

    public LinkedList<InputData> getInputData() {
        return inputData;
    }
}
