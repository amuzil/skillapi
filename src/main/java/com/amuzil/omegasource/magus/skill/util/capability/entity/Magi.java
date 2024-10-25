package com.amuzil.omegasource.magus.skill.util.capability.entity;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;

public class Magi {


    private final Data capabilityData;

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
        return new LinkedList<>();
    }
}
