package com.amuzil.omegasource.magus.skill.util.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Very different from IDataTrait. This class lets you access data and do things with it,
 * whereas IDataTrait merely stores it.
 */
@AutoRegisterCapability
public interface IData extends INBTSerializable<CompoundTag> {

}
