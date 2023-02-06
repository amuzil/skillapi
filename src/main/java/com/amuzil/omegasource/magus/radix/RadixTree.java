package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.forms.Form;

public class RadixTree {
    private final Node root;
    private Node active;

    public RadixTree(Node root) {
        this.root = root;
//        this.branch = new Branch();
        this.active = root;
    }

//    public <T> boolean registerLeaf(Class<Leaf<T>> type, Leaf<T> leaf) {
//        return branch.registerLeaf(type, leaf);
//    }

    public void burn() {
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

    private void setActive(Node node) {
        active = node;

//        if (active.onEnter() != null) {
//            active.onEnter().accept(branch);
//        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().register(this::terminate, () -> {
            });
        }
    }

    // Called when either the node's terminate condition is fulfilled or all active child conditions have expired
    private void terminate() {
//        if (active.onTerminate() != null) {
//            active.onTerminate().accept(branch);
//        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        start();
    }

    public void moveDown(Form executedForm) {
//        if (active.onLeave() != null) {
//            active.onLeave().accept(branch);
//        }

        if(active.children().size() == 0) return;

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        setActive(active.children().get(executedForm));
    }

    public void expire() {
        terminate();
    }
}