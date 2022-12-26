package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Figure out how to save this data when MC wants to save but nothing has changed/.
 */
public class LivingData implements Data {

    //Data Traits to add:
    // private List<SkillBase> skills;
//    private SkillData data;
//    private List<SkillCategory> categories;
//    private SkillCategory activeCategory;
//    //Need to add energy mechanic; stored here
//    //Need to add config and *global* skill modifiers.
//    //Need to add something for inputs
//    private Set<Consumer> activeListeners;
//    //Miscellaneous data to save

    //The amount of data traits the player has should not change after initialisation.
    private List<DataTrait> traits = new ArrayList<>();
    private List<SkillCategory> categories = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();
    private boolean isDirty;

    public LivingData() {
        fillTraits();
        //TODO: Data generation methods for each skill

        fillCategories();
        fillSkills();
        markDirty();
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        traits.forEach(trait -> {
            if (trait.isDirty()) {
                tag.put(trait.getName(), trait.serializeNBT());
            }
        });
        return tag;
    }



    @Override
    public void deserializeNBT(CompoundTag nbt) {
        traits.forEach(trait -> {
            trait.deserializeNBT((CompoundTag) nbt.get(trait.getName()));
        });
    }

    public void fillTraits() {
        traits.addAll(Registries.DATA_TRAITS.get().getValues());
    }

    //When players move to versions with new techniques and such, we'll have to use these to accomodate.
    public void addTraits(List<DataTrait> dataTraits) {
        traits.addAll(dataTraits);
    }

    public void addTrait(DataTrait trait) {
        traits.add(trait);
    }

    //Ideally, these delete methods are *never* used, because each piece of content
    //added to the mod should be final.
    public void removeTrait(DataTrait trait) {
        traits.remove(trait);
    }

    public void removeTraits(List<DataTrait> dataTraits) {
        traits.removeAll(dataTraits);
    }

    public void fillCategories() {
        categories.addAll(Registries.SKILL_CATEGORIES.get().getValues());
    }

    public List<SkillCategory> getAllSkillCategories() {
        return this.categories;
    }

    public void fillSkills() {
        skills.addAll(Registries.SKILLS.get().getValues());
    }
    public List<Skill> getAllSkills() {
        return this.skills;
    }

    public SkillData getSkillData() {
        return null;
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
        for (DataTrait trait : traits)
            if (trait.isDirty()) {
                markDirty();
                return true;
            }
        //TODO: Add a check for all kinds of data, not just DataTraits
        return this.isDirty;
    }
}
