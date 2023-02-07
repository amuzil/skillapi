package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;

public class RadixTree {
    private final Node root;
    private Node active;
    private Form lastActivated = null;
    private RadixPath path;

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
        path = new RadixPath();
    }

    private void setActive(Node node) {
        active = node;

        if (active.onEnter() != null) {
            active.onEnter().accept(this);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().register(this::terminate, () -> {
            });
        }
    }

    // Called when either the node's terminate condition is fulfilled or all active child conditions have expired
    private void terminate() {
        if (active.onTerminate() != null) {
            active.onTerminate().accept(this);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        start();
    }

    public void moveDown(Form executedForm) {
        //add the last Node to the activation Path and store its ModifierData's
        path.addStep(this.lastActivated, active.getModifiers().stream().map(Modifier::data).toList());
        this.lastActivated = executedForm;

        //todo remove this its just for testing
        if(active.getModifiers().size() > 0) active.getModifiers().forEach(modifier -> modifier.print());

        if(active.children().size() == 0) return;

        if (active.onLeave() != null) {
            active.onLeave().accept(this);
        }


        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        setActive(active.children().get(executedForm));
    }

    public void expire() {
        terminate();
    }
}