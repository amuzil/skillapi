package com.amuzil.omegasource.magus.skill.util.data;

import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO: Make this an implementation rather than a class.
//E.g SizeTrait vs ElementTrait or something are both SkillTraits but....
public class SkillData implements DataTrait {

    List<SkillTrait> skillTraits = new ArrayList<>();
    private boolean isDirty = false;

    @Override
    public String getName() {
        return "skillData";
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public void markClean() {
        this.isDirty = false;
    }

    @Override
    public boolean isDirty() {
        for (SkillTrait trait : skillTraits)
            if (trait.isDirty()) {
                markDirty();
                return true;
            }

        return this.isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        skillTraits.forEach(skillTrait -> {
            if (skillTrait.isDirty())
                tag.put(skillTrait.getName() + "Trait", skillTrait.serializeNBT());
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        markClean();
        try {
            skillTraits.forEach(skillTrait -> skillTrait.deserializeNBT
                    ((CompoundTag) Objects.requireNonNull(nbt.get(skillTrait.getName() + "Trait"))));
        } catch (NullPointerException e) {
            RadixUtil.getLogger().error("Something has gone seriously wrong:" +
                    "A skill trait hasn't been carried over from the registry.");
            e.printStackTrace();
        }
    }
}
