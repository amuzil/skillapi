package com.amuzil.omegasource.magus.radix;

public class RadixBranch {
     public RadixPath path;
     public Node next;

     public RadixBranch(RadixPath path, Node next) {
        this.path = path;
        this.next = next;
     }
}
