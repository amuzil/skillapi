package com.amuzil.omegasource.magus.skill.util.capability.entity;

import com.amuzil.omegasource.magus.radix.RadixTree;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Very different from IDataTrait. This class lets you access data and do things with it,
 * whereas IDataTrait merely stores it.
 */
//@AutoRegisterCapability
public interface Data extends INBTSerializable<CompoundTag> {

    /* These are used to make everything stored in the class serialised/unserialised.
        Not for generic purposes like DataTrait.
     */
    void markDirty();

    void markClean();

    boolean isDirty();

    RadixTree getTree();


}
