package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.util.traits.IDataTrait;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class LivingData implements IData {

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
    private final List<IDataTrait> traits;

    public LivingData() {
        traits = new ArrayList<>();
        fillTraits();
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        traits.forEach(trait -> tag.put(trait.getName(), trait.serializeNBT()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        traits.forEach(trait -> trait.deserializeNBT((CompoundTag) nbt.get(trait.getName())));
    }

    public void fillTraits() {
        traits.addAll(Registries.DATA_TRAITS.get().getValues());
    }

    //When players move to versions with new techniques and such, we'll have to use these to accomodate.
    public void addTraits(List<IDataTrait> dataTraits) {
        traits.addAll(dataTraits);
    }

    public void addTrait(IDataTrait trait) {
        traits.add(trait);
    }

    //Ideally, these delete methods are *never* used, because each piece of content
    //added to the mod should be final.
    public void removeTrait(IDataTrait trait) {
        traits.remove(trait);
    }

    public void removeTraits(List<IDataTrait> dataTraits) {
        traits.removeAll(dataTraits);
    }
}
