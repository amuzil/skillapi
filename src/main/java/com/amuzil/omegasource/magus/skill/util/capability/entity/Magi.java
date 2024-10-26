package com.amuzil.omegasource.magus.skill.util.capability.entity;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.data.SkillCategoryData;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Magi {


    private final Data capabilityData;
    private List<SkillData> skillData;
    private List<SkillCategoryData> skillCategoryData;

    public Magi(Data capabilityData) {
        this.capabilityData = capabilityData;
    }

    public static Magi get(LivingEntity entity) {
        return new Magi(CapabilityHandler.getCapability(entity, CapabilityHandler.LIVING_DATA));
    }

    public LivingDataCapability.LivingDataCapabilityImp getMagusData() {
        return (LivingDataCapability.LivingDataCapabilityImp) capabilityData;
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

    public static boolean isEntitySupported(Entity entity) {
        return entity instanceof LivingEntity;
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

    }

    public void onDeath() {

    }


}
