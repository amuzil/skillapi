package com.amuzil.omegasource.magus.skill.util.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface ILivingData extends INBTSerializable<CompoundTag> {

}
