package com.amuzil.omegasource.magus.skill.util.capability.entity;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.data.SkillCategoryData;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Magi {


    private final Data capabilityData;
    private final LivingEntity magi;
    private List<SkillData> skillData;
    private List<SkillCategoryData> skillCategoryData;

    public Magi(Data capabilityData, LivingEntity entity) {
        this.capabilityData = capabilityData;
        this.magi = entity;

        // Initialise skilldata.
        this.skillData = new ArrayList<>();
        for (Skill skill : Registries.skills)
            skillData.add(new SkillData(skill));
    }

    @Nullable
    public static Magi get(LivingEntity entity) {
        LivingDataCapability.LivingDataCapabilityImp cap = ((LivingDataCapability.LivingDataCapabilityImp) (CapabilityHandler.getCapability(entity, CapabilityHandler.LIVING_DATA)));
        if (cap == null) {
            // Capability isn't ready yet.
            return null;
        }
        return cap.getMagi(entity);
    }

    public static boolean isEntitySupported(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public LivingDataCapability.LivingDataCapabilityImp getMagusData() {
        return (LivingDataCapability.LivingDataCapabilityImp) capabilityData;
    }

    // Need to sync this with given skills, traits, e.t.c
    public boolean isDirty() {
        return false;
    }

    public RadixTree getTree() {
        return capabilityData.getTree();
    }

    public List<DataTrait> getTraits() {
        return getMagusData().getTraits();
    }

    public List<SkillTrait> getSkillTraits(Skill skill) {
        // Get skill data based on the skill,
        // then get the list of its traits.
        // Empty for now...
        return getSkillData(skill).getSkillTraits();
    }

    // Returns appropriate SkillData from a list of SkillData
    public SkillData getSkillData(Skill skill) {
        return getSkillData(skill.getId());
    }

    public SkillData getSkillData(ResourceLocation id) {
        return skillData.stream().filter(skillData1 -> skillData1.getSkillId().equals(id)).toList().get(0);
    }

    public SkillData getSkillData(String id) {
        ResourceLocation loc = new ResourceLocation(id);
        return getSkillData(loc);
    }

    // Called per tick
    public void onUpdate() {
        List<Skill> skills = Registries.getSkills();
        for (Skill skill : skills) {
            if (getSkillData(skill).canUse()) {
                skill.tick(getMagi(), getTree());
            }
        }
    }

    public void onDeath() {

    }

    public LivingEntity getMagi() {
        return this.magi;
    }

    public CompoundTag serialiseNBT() {
        CompoundTag tag = new CompoundTag();
        if (isDirty()) {
            // TODO: Figure out if I need to use the returned tags from each of these values....
            skillCategoryData.forEach(SkillCategoryData::serializeNBT);
            skillData.forEach(SkillData::serializeNBT);
            tag = capabilityData.serializeNBT();
        }
        return tag;
    }

    public void deserialiseNBT(CompoundTag tag) {
        skillCategoryData.forEach(skillCategoryData1 -> skillCategoryData1.deserializeNBT(tag));
        skillData.forEach(skillData1 -> skillData1.deserializeNBT(tag));
        capabilityData.deserializeNBT(tag);
    }

}
