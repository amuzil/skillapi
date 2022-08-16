package com.amuzil.omegasource.magus.radix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RadixTree {
    private final Node root;
    private final Branch branch;
    private final List<Condition> activeConditions;

    private Node active;

    public RadixTree(Node root) {
        this.root = root;
        this.branch = new Branch();
        this.active = root;
        this.activeConditions = new ArrayList<>();
    }

    public <T> boolean registerLeaf(Class<Leaf<T>> type, Leaf<T> leaf) {
        return branch.registerLeaf(type, leaf);
    }

    public void burn() {
        activeConditions.forEach(Condition::unregister);
        activeConditions.clear();

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        active = null;

        branch.burn();
    }

    public void start() {
        branch.reset(root);
        setActive(root);
    }

    public void setActive(Node node) {
        active = node;

        if (active.onEnter() != null) {
            active.onEnter().accept(branch);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().register(this::terminate, () -> {
            });
        }

        active.children().forEach((condition, child) -> {
            activeConditions.add(condition);
            condition.register(() -> {
                branch.addStep(condition, child);
                moveDown(child);
            }, () -> expire(condition));
        });
    }

    // Called when either the node's terminate condition is fulfilled or all active child conditions have expired
    public void terminate() {
        activeConditions.forEach(Condition::unregister);

        if (active.onTerminate() != null) {
            active.onTerminate().accept(branch);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        activeConditions.clear();

        start();
    }

    public void moveDown(Node child) {
        if (active.onLeave() != null) {
            active.onLeave().accept(branch);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        Iterator<Condition> iterator = activeConditions.iterator();
        while (iterator.hasNext()) {
            Condition condition = iterator.next();
            condition.unregister();
            iterator.remove();
        }

        setActive(child);
    }

    public void expire(Condition condition) {
        condition.unregister();
        activeConditions.remove(condition);
        if (activeConditions.isEmpty()) {
            terminate();
        }
    }
}