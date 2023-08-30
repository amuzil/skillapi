package com.amuzil.omegasource.magus.skill.effects;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.forms.Form;

public class EffectTreeBuilder {

    private RadixTree tree;
    static EffectTreeBuilder builder;
    public static EffectTreeBuilder instance() {
        if (builder != null)
            return builder;
        else builder = new EffectTreeBuilder();
        return builder;
    }

    //Creates an Effect runnable to add to the tree.
    public EffectTreeBuilder addEffect(Form form, Effect effect) {
        return this;
    }
    public RadixTree build() {
        return tree;
    }
}
