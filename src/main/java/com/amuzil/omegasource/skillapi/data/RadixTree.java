package com.amuzil.omegasource.skillapi.data;

import java.util.Iterator;
import java.util.List;

public class RadixTree {
    RadixNode root;

    RadixNode active;
    List<Condition> activeConditions;

    void register() {
        registerNode(root);
    }

    void registerNode(RadixNode node) {
        active = node;

        if (node.fallbackCondition != null) {
            activeConditions.add(node.fallbackCondition);
            node.fallbackCondition.register(() -> terminate(node), () -> terminate(node.parent));
        }

        node.children.forEach((condition, child) -> {
            activeConditions.add(condition);
            condition.register(() -> moveDown(child), () -> expire(condition));
        });
    }

    void moveDown(RadixNode child) {
        Iterator<Condition> iterator = activeConditions.iterator();
        while (iterator.hasNext()) {
            Condition condition = iterator.next();
            condition.unregister();
            iterator.remove();
        }

        registerNode(child);
    }

    void expire(Condition condition) {
        condition.unregister();
        activeConditions.remove(condition);
        if (activeConditions.isEmpty()) {
            terminate(active);
        }
    }

    void terminate(RadixNode node) {
        Iterator<Condition> iterator = activeConditions.iterator();
        while (iterator.hasNext()) {
            Condition condition = iterator.next();
            condition.unregister();
            iterator.remove();
        }

        Runnable action = node.fallback;
        while (action == null && node.parent != null) {
            node = node.parent;
            action = node.fallback;
        }

        if (action != null) {
            action.run();
        }
    }
}