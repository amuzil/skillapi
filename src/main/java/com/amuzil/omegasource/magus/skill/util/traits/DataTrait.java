package com.amuzil.omegasource.magus.skill.util.traits;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface DataTrait extends INBTSerializable<CompoundTag> {

    String getName();

    /* Remember to call these in *ever* setter you have for each trait! */
    /* Methods for whether to save the data. */
    void markDirty();

    /* Do not use this ever. This is only used by LivingData upon unserialisation. */
    void markClean();

    boolean isDirty();


}
