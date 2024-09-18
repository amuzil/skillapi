package com.amuzil.omegasource.magus.radix.path;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Creates a path for a given object.
public class PathBuilder {
    private ConditionPath path;
    private RadixTree.ActivationType type;
    private HashMap<RadixTree.ActivationType, ConditionPath> finalPath;

    public static PathBuilder instance;
    public static PathBuilder getInstance() {
        if (instance == null)
            instance = new PathBuilder();
        instance.reset();
        return instance;
    }

    public PathBuilder path(Condition... conditions) {
        List<ModifierData> emptyData = new ArrayList<>();
        if (path == null)
            path = new ConditionPath(List.of(conditions));
        else {
            for (Condition condition : conditions) {
                path.addStep(condition, emptyData);
            }
        }
        return this;
    }

    public PathBuilder type(RadixTree.ActivationType type) {
        this.type = type;
        return this;
    }
    public HashMap<RadixTree.ActivationType, ConditionPath> build() {
        finalPath = new HashMap<>();
        finalPath.put(type, path);
        reset();
        return this.finalPath;
    }

    public void reset() {
        this.type = null;
        this.path = null;
    }
}
