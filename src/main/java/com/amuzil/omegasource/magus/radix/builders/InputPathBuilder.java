package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Creates a path for a given object. Designed for things which directly require raw input.

/**
 * Creates an input-based ConditionPath for a given object. Useful for forms or things that exist predominantly client-side.
 */
public class InputPathBuilder {
    public static InputPathBuilder instance;
    private ConditionPath path;
    // Todo: Change this to input type? Skills should use the activation type, forms input type?
    private RadixTree.InputType type;

    public static InputPathBuilder getInstance() {
        if (instance == null) instance = new InputPathBuilder();
        instance.reset();
        return instance;
    }

    public InputPathBuilder path(Condition... conditions) {
        PathBuilder.addSteps(path, conditions);
        return this;
    }

    public InputPathBuilder type(RadixTree.InputType type) {
        this.type = type;
        return this;
    }

    public HashMap<RadixTree.InputType, ConditionPath> build() {
        HashMap<RadixTree.InputType, ConditionPath> finalPath = new HashMap<>();
        finalPath.put(type, path);
        reset();
        return finalPath;
    }

    public void reset() {
        this.type = null;
        this.path = null;
    }
}
