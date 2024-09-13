package com.amuzil.omegasource.magus.radix;


public class RadixBranch {
    // Class that represents a valid condition path leading from a previous Node and stores the Condition(s)

    public ConditionPath path;
    public Node next;

    public RadixBranch(ConditionPath path) {
        this(path, new Node(true));
    }

    public RadixBranch(ConditionPath path, Node next) {
        this.path = path;
        this.next = next;
     }

    @Override
    public String toString() {
        return "RadixBranch[Conditions=" + path + "]";
    }
}
