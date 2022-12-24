package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.skill.util.traits.IDataTrait;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class LivingDataCapability implements LivingDataInterface {

    private List<IDataTrait> traits = new ArrayList<>();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (IDataTrait trait : traits)
            tag.put(trait.getName(), trait.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (IDataTrait trait : traits)
            trait.deserializeNBT((CompoundTag) nbt.get(trait.getName()));
    }
}
