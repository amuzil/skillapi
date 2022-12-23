package com.amuzil.omegasource.magus.skill.util.traits;

import com.amuzil.omegasource.magus.skill.skill.SkillBase;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class LivingDataTrait implements DataTrait {


    private List<SkillBase> skills;
    private SkillData data;
    private List<SkillCategory> categories;
    private SkillCategory activeCategory;
    //Need to add energy mechanic; stored here
    //Need to add config and *global* skill modifiers.
    //Need to add something for inputs
    private Set<Consumer> activeListeners;
    //Miscellaneous data to save
    private List<DataTrait> traits;


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
