package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class LivingDataCapability implements LivingDataInterface {

    private List<DataTrait> traits = new ArrayList<>();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (DataTrait trait : traits)
            tag.put(trait.getName(), trait.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (DataTrait trait : traits)
            trait.deserializeNBT((CompoundTag) nbt.get(trait.getName()));
    }
}
