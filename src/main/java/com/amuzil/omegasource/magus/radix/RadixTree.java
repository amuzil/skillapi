package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RadixTree {
    private final Node root;
    private final List<Condition> activeConditions;
    private Node active;

    public RadixTree(Node root) {
        this.root = root;
//        this.branch = new Branch();
        this.active = root;
        this.activeConditions = new ArrayList<>();
    }

//    public <T> boolean registerLeaf(Class<Leaf<T>> type, Leaf<T> leaf) {
//        return branch.registerLeaf(type, leaf);
//    }

    public void burn() {
        activeConditions.forEach(Condition::unregister);
        activeConditions.clear();

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        active = null;

//        branch.burn();
    }

    public void start() {
//        branch.reset(root);
        setActive(root);
    }

    public void setActive(Node node) {
        active = node;

//        if (active.onEnter() != null) {
//            active.onEnter().accept(branch);
//        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().register(this::terminate, () -> {
            });
        }

        active.children().forEach((form, child) -> {
            Condition formCondition = new ConditionBuilder()
                    .fromInputData(form.getInputData())
                    .build();
            activeConditions.add(formCondition);
            Magus.inputModule.registerInputData(formCondition, form, () -> {
                moveDown(child);
            }, () -> expire(formCondition));
        });
    }

    // Called when either the node's terminate condition is fulfilled or all active child conditions have expired
    public void terminate() {
        activeConditions.forEach(Condition::unregister);

//        if (active.onTerminate() != null) {
//            active.onTerminate().accept(branch);
//        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        activeConditions.clear();

        start();
    }

    public void moveDown(Node child) {
//        if (active.onLeave() != null) {
//            active.onLeave().accept(branch);
//        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }
//
        Iterator<Condition> iterator = activeConditions.iterator();
        while (iterator.hasNext()) {
            Condition condition = iterator.next();
            condition.unregister();
            iterator.remove();
        }

        setActive(child);
    }

    public void expire(Condition condition) {
        Magus.inputModule.unregisterCondition(condition);
        activeConditions.remove(condition);
        if (activeConditions.isEmpty()) {
            terminate();
        }
    }
}