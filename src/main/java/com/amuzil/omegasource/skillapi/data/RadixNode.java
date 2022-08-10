package com.amuzil.omegasource.skillapi.data;

import java.util.Map;
import java.util.function.Consumer;

public class RadixNode {
    // If a condition is fulfilled, the active node moves down to the mapped child node
    Map<Condition, RadixNode> children;

    // Called when the active node is moved down from the parent node to this node
    Consumer<RadixBranch> onEnter;
    // Called when the active node is moved down from this node to a child node
    Consumer<RadixBranch> onLeave;
    // Called when the active node is moved up to the root node because either all children's conditions have expired or the terminate condition has been fulfilled
    Consumer<RadixBranch> onTerminate;
    // If this condition is fulfilled, the active node will be terminated. If it expires, nothing special happens. It doesn't have to expire for the branch to terminate
    Condition terminateCondition;

    public RadixNode () {
        this.init();
    }

    void init() {
        //All can be anything. TODO: Fix.
        children = null;
        onEnter = null;
        onLeave = null;
        onTerminate = null;
        terminateCondition = null;
    }
}

class RootNode extends RadixNode {
    // This is the only node where onEnter is called not when this node is moved down to, but rather when a (sub)child node terminates

    @Override
    void init() {
        onTerminate = null;
        terminateCondition = null;
    }
}

class EndNode extends RadixNode {

    @Override
    void init() {
        children = null;
        //@Nonnull
        onEnter = null;
        onLeave = null;
        onTerminate = null;
        terminateCondition = null;
    }
}
