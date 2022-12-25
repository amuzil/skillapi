package com.amuzil.omegasource.magus.skill.util.data;

import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

//TODO: Make this an implementation rather than a class.
//E.g SizeTrait vs ElementTrait or something are both SkillTraits but....
public class SkillData implements DataTrait {

    List<SkillTrait> skillTraits = new ArrayList<>();


    @Override
    public String getName() {
        return "skillData";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        skillTraits.forEach(skillTrait -> tag.put(skillTrait.getName() + "Trait", skillTrait.serializeNBT()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        skillTraits.forEach(skillTrait -> skillTrait.deserializeNBT
                ((CompoundTag) nbt.get(skillTrait.getName() + "Trait")));
    }
}
