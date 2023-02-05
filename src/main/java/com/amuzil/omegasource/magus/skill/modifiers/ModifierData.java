package com.amuzil.omegasource.magus.skill.modifiers;

import net.minecraftforge.common.util.INBTSerializable;

public abstract class ModifierData implements INBTSerializable {

    public ModifierData() {
    }

    /* this method should be overwritten by the specific implementation
       of ModifierData, and encapsulates merging two copies of the same
       ModifierData into one set. This base method just ensures we aren't
       merging two separate types */
    public void add(ModifierData modifierData) {
        if(!modifierData.getClass().equals(this.getClass())) {
            //todo logging - tried to merge 2 different types of ModifierData together
        }
    }
}
