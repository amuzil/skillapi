package com.amuzil.omegasource.skillapi.data;

import java.util.HashMap;
import java.util.LinkedList;

public class RadixBranch {
    LinkedList<Step> path;

    //TODO: Fix this
    HashMap<Class<RadixLeaf<?>>, RadixLeaf<?>> leaves;

    void init() {
        path = new LinkedList<>();
        leaves = new HashMap<>();
    }

    void burn() {
        path.clear();
        leaves.values().forEach(RadixLeaf::burn);
        leaves.clear();
    }

    void reset(RadixNode root) {
        path.clear();
        addStep(null, root);
        leaves.values().forEach(RadixLeaf::reset);
    }

    void addStep(Condition activator, RadixNode node) {
        path.add(new Step(activator, node));
    }

    <T> boolean registerLeaf(Class<RadixLeaf<T>> type, RadixLeaf<T> leaf) {
        if (leaves.containsKey(type)) {
            return false;
        } else {
            leaves.put(type, leaf);
            return true;
        }
    }

    <T> void resetLeaf(Class<RadixLeaf<T>> type) {
        leaves.get(type).reset();
    }

    <T> T measureLeaf(Class<RadixLeaf<T>> type) {
        return (T) leaves.get(type).measure();
    }

    static class Step {
        Condition activator;
        RadixNode node;


        public Step(Condition activator, RadixNode node) {
            this.activator = activator;
            this.node = node;
        }

        public Step() {
            this.init();
        }

        void init() {
            activator = null;
            //Nonnull
            node = new RadixNode();
        }

        public Condition getActivator() {
            return activator;
        }

        public RadixNode getNode() {
            return node;
        }
    }
}

