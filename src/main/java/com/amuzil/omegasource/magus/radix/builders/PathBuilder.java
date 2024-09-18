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
 * Creates a path for a given objecct. Uses different activation types to denote what kind of tree to use these conditions for.
 * If using raw input, use InputPathBuilder instead.
 */
public class PathBuilder {
    public static PathBuilder instance;
    private ConditionPath path;
    // Todo: Change this to input type? Skills should use the activation type, forms input type?
    private RadixTree.ActivationType type;

    protected static void addSteps(ConditionPath path, Condition... conditions) {
        List<ModifierData> emptyData = new ArrayList<>();
        if (path == null) path = new ConditionPath(List.of(conditions));
        for (Condition condition : conditions) {
            path.addStep(condition, emptyData);
        }
    }

    public static PathBuilder getInstance() {
        if (instance == null) instance = new PathBuilder();
        instance.reset();
        return instance;
    }

    public PathBuilder path(Condition... conditions) {
        addSteps(path, conditions);
        return this;
    }

    public PathBuilder type(RadixTree.ActivationType type) {
        this.type = type;
        return this;
    }

    public HashMap<RadixTree.ActivationType, ConditionPath> build() {
        HashMap<RadixTree.ActivationType, ConditionPath> finalPath = new HashMap<>();
        finalPath.put(type, path);
        reset();
        return finalPath;
    }

    public void reset() {
        this.type = null;
        this.path = null;
    }
}
