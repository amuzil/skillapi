package com.amuzil.omegasource.magus.skill.effects;

import com.amuzil.omegasource.magus.radix.Node;
import com.amuzil.omegasource.magus.radix.NodeBuilder;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.forms.Form;

public class EffectTreeBuilder {

    private NodeBuilder tree;
    private Node current;
    static EffectTreeBuilder builder;
    public static EffectTreeBuilder instance() {
        if (builder != null)
            return builder;
        else builder = new EffectTreeBuilder();
        return builder;
    }

    public EffectTreeBuilder start() {
        tree = NodeBuilder.middle();
        return this;
    }

    //Creates an Effect runnable to add to the tree.
    public EffectTreeBuilder addEffect(Form form, Effect effect) {
        tree.addChild(form, current);
        return this;
    }


    public RadixTree build() {
        return new RadixTree(tree.build());
    }
}
