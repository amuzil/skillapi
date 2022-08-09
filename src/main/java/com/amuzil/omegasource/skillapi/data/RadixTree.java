package com.amuzil.omegasource.skillapi.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RadixTree {
    final RadixNode root;

    RadixBranch branch;
    RadixNode active;
    List<Condition> activeConditions;

    void init() {
        root = nonnull;
        branch = new RadixBranch();
        active = null;
        activeConditions = new HashMap<>();
    }

    <T> boolean registerLeaf(Class<RadixLeaf<T>> type, RadixLeaf<T> leaf) {
        return branch.registerLeaf(type, leaf);
    }

    void burn() {
        activeConditions.forEach(Condition::unregister);
        activeConditions.clear();

        if (active.terminateCondition != null) {
            active.terminateCondition.unregister();
        }

        active = null;

        branch.burn();
    }

    void start() {
        branch.reset(root);
        setActive(root);
    }

    void setActive(Node node) {
        active = node;

        if (active.onEnter != null) {
            active.onEnter.accept(branch);
        }

        if (active.terminateCondition != null) {
            active.terminateCondition.register(() -> terminate(node), () -> {});
        }

        active.children.forEach((condition, child) -> {
            activeConditions.add(condition);
            condition.register(() -> {
                branch.addStep(condition, child)
                moveDown(child);
            }, () -> expire(condition));
        });
    }

    // Called when either the node's terminate condition is fulfilled or all active child conditions have expired
    void terminate(Node node) {
        activeConditions.forEach(Condition::unregister);

        if (active.onTerminate != null) {
            active.onTerminate.accept(branch);
        }

        if (active.terminateCondition != null) {
            active.terminateCondition.unregister();
        }

        activeConditions.clear();

        start();
    }

    void moveDown(Node child) {
        if (active.onLeave != null) {
            active.onLeave.accept(branch);
        }

        if (active.terminateCondition != null) {
            active.terminateCondition.unregister();
        }

        Iterator<Condition> iterator = activeConditions.iterator();
        while (iterator.hasNext()) {
            Condition condition = iterator.next();
            condition.unregister();
            iterator.remove();
        }

        setActive(child);
    }

    void expire(Condition condition) {
        condition.unregister();
        activeConditions.remove(condition);
        if (activeConditions.empty()) {
            terminate(active);
        }
    }
}