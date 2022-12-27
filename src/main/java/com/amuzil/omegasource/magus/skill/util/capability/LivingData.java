package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;

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
    private final List<DataTrait> traits = new ArrayList<>();
    private final List<SkillCategory> categories = new ArrayList<>();
    private final List<Skill> skills = new ArrayList<>();
    private boolean isDirty;

    //Gets the tree from the event bus.
    private RadixTree tree;

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
            if (trait.isDirty() || isDirty()) {
                tag.put(trait.getName(), trait.serializeNBT());
            }
        });
        return tag;
    }


    @Override
    public void deserializeNBT(CompoundTag nbt) {
        markClean();
        traits.forEach(trait -> trait.deserializeNBT((CompoundTag) nbt.get(trait.getName())));
    }


    public void setTree(RadixTree tree) {
        this.tree = tree;
        markDirty();
    }

    public RadixTree getTree() {
        return tree;
    }

    public void fillTraits() {
        traits.addAll(Registries.DATA_TRAITS.get().getValues());
    }

    public List<DataTrait> getTraits() {
        return this.traits;
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

    @Nullable
    public DataTrait getTrait(String name) {
        for (DataTrait trait : getTraits())
            if (trait.getName().equals(name))
                return trait;

        return null;

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
        //TODO: Add a check for all kinds of data, not just DataTraits
        return this.isDirty;
    }
}
