package com.amuzil.omegasource.magus.skill.util.data;

import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//TODO: Make this an implementation rather than a class.
//E.g SizeTrait vs ElementTrait or something are both SkillTraits but....
public class SkillData implements DataTrait {

    List<SkillTrait> skillTraits;
    //The reason we're using a resource location and not the actual Skill object is because
    //it's much easier to serialise a String and then get a skill from it.
    ResourceLocation skillId;
    private boolean isDirty = false;

    public SkillData(ResourceLocation skillId) {
        this.skillId = skillId;
        this.skillTraits = getSkill().getTraits();
    }


    public SkillData(Skill skill) {
        this(skill.getId());
    }

    @Override
    public String getName() {
        return "skillData-" + getSkillId();
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
        tag.putString("Skill ID", skillId.toString());
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
            skillId = ResourceLocation.tryParse(nbt.getString("Skill ID"));
            skillTraits.forEach(skillTrait -> skillTrait.deserializeNBT
                    ((CompoundTag) Objects.requireNonNull(nbt.get(skillTrait.getName() + "Trait"))));
        } catch (NullPointerException e) {
            RadixUtil.getLogger().error("Something has gone seriously wrong:" +
                    "A skill trait hasn't been carried over from the registry.");
            e.printStackTrace();
        }
    }


    public List<SkillTrait> getSkillTraits() {
        return skillTraits;
    }

    public ResourceLocation getSkillId() {
        return skillId;
    }

    public Skill getSkill() {
        return Registries.SKILLS.get().getValue(getSkillId());
    }

    public List<SkillTrait> getFilteredTraits(Predicate<? super SkillTrait> filter) {
        return getSkillTraits().stream().filter(filter)
                .collect(Collectors.toList());
    }

    @Nullable
    public SkillTrait getTrait(String name) {
        for (SkillTrait trait : getSkillTraits())
            if (trait.getName().equals(name))
                return trait;

        return null;
    }

    public void reset() {
        for (SkillTrait trait : getSkillTraits())
            trait.reset();
    }
}
