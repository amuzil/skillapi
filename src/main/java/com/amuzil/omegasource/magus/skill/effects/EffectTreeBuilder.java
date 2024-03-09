package com.amuzil.omegasource.magus.skill.effects;

import com.amuzil.omegasource.magus.radix.Node;
import com.amuzil.omegasource.magus.radix.NodeBuilder;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.forms.Form;

/**
 * Creates Effects given a set of Forms.
 * To use Effects together, check out SkillBuilder
 */
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

    //Adds a required Form to the tree.
    public EffectTreeBuilder addForm(Form form) {
        tree.addChild(form, current);
        return this;
    }


    public RadixTree build() {
        return new RadixTree(tree.build());
    }
}
