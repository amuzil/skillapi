package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.RadixUtil;
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

    protected static void addSteps(ConditionPath path, Condition... conditions) {
        List<ModifierData> emptyData = new ArrayList<>();
        if (path == null)
            path = new ConditionPath(List.of(conditions));
        for (Condition condition : conditions) {
            RadixUtil.getLogger().debug(condition);
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
        // Reset condition path
//        this.path = new ConditionPath();
        return this;
    }

    public PathBuilder path(Condition... conditions) {
        addSteps(path, conditions);
        return this;
    }

    public List<ConditionPath> build() {
        List<ConditionPath> finalPath = new ArrayList<>();
        finalPath.addAll(paths);
        RadixUtil.getLogger().debug(finalPath);
        return finalPath;
    }

    public void reset() {
        this.path = null;
        if (paths != null)
            this.paths.clear();
    }
}
