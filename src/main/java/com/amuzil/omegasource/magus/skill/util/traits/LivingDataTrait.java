package com.amuzil.omegasource.magus.skill.util.traits;

import com.amuzil.omegasource.magus.skill.skill.SkillBase;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.SkillData;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class LivingDataTrait implements DataTrait {


    private List<SkillBase> skills;
    private SkillData data;
    private List<SkillCategory> categories;


    @Override
    public String getName() {
        return "livingDataTraits";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();


        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
