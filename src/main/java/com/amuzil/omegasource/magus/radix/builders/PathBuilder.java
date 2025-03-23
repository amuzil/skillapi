package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.condition.input.FormActivatedCondition;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// Creates a path for a given object. Designed for things which directly require raw input.

/**
 * Creates a path for a given objecct. Uses different activation types to denote what kind of tree to use these conditions for.
 * If using raw input, use InputPathBuilder instead.
 */
public class PathBuilder {
    public static PathBuilder instance;
    private List<ConditionPath> paths;
    private ConditionPath path;
    // Todo: Change this to input type? Skills should use the activation type, forms input type?

    protected void addSteps(Condition... conditions) {
        List<ModifierData> emptyData = new ArrayList<>();
        path = new ConditionPath(List.of(conditions));
        for (Condition condition : conditions) {
            path.addStep(condition, emptyData);
        }
    }

    public static PathBuilder getInstance() {
        if (instance == null) instance = new PathBuilder();
        instance.reset();
        return instance;
    }

    public PathBuilder finalisePath(ConditionPath path) {
        if (paths == null)
            this.paths = new LinkedList<>();
        this.paths.add(path);
        return this;
    }

    public PathBuilder finalisePath() {
        finalisePath(path);
        return this;
    }

    public PathBuilder path(Condition... conditions) {
        addSteps(conditions);
        return this;
    }

    public List<ConditionPath> build() {
        List<ConditionPath> finalPath = new ArrayList<>(paths);
        return finalPath;
    }

    public void reset() {
        this.path = null;
        if (paths != null)
            this.paths.clear();
    }
}
