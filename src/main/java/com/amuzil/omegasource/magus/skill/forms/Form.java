package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;

import java.util.ArrayList;
import java.util.List;

public class Form {
    private final String name;

    public Form(String name) {
        this.name = name;
        Registries.registerForm(this);
    }

    public Form() { // Create null Form to fix random NullPointerException
        this.name = null;
    }

    /**
     * Just in case. I don't think we care about the MOD_ID as forge handles that.
     * @param name
     * @param modID
     */
    public Form(String name, String modID) {
        this(name);
    }

    public String name() {
        return name;
    }

    // Can be freely overridden to alter a Form's path.
    // Specifically, if InputData isn't specific enough for what you want to, feel free to alter it here.
    public ConditionPath createPath(List<Condition> conditions) {
        ConditionPath path = new ConditionPath();
        List<ModifierData> emptyData = new ArrayList<>();
        for (Condition condition : conditions) {
            path.addStep(condition, emptyData);
        }

        return path;
    }

    @Override
    public String toString() {
        return "Form[ " + name().toUpperCase() + " ]";
    }
}
