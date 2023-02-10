package com.amuzil.omegasource.magus.skill.modifiers.api;

import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;


// actually adding data is handled by sub-class implementations, this is just for generics.
public abstract class ModifierData implements DataTrait {

    public ModifierData() {
    }

    /* this method should be overwritten by the specific implementation
       of ModifierData, and encapsulates merging two copies of the same
       ModifierData into one set. This base method just ensures we aren't
       merging two separate types */
    public void add(ModifierData modifierData) {
        if(!modifierData.getClass().equals(this.getClass())) {
            //todo logging - tried to merge 2 different types of ModifierData together
        } else {
            mergeFields(modifierData);
            markDirty();
        }
    }

    protected abstract void mergeFields(ModifierData modifierData);

    public abstract void print();

    public abstract void reset();
    public abstract ModifierData copy();
}
