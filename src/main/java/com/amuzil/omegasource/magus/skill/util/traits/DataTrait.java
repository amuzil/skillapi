package com.amuzil.omegasource.magus.skill.util.traits;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface DataTrait extends INBTSerializable<CompoundTag> {
    String getName();
}
