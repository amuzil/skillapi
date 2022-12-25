package com.amuzil.omegasource.magus.registry;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

/**
 * All custom registries go here.
 */
@Mod.EventBusSubscriber(modid = Magus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registries {

    public static void init() {
    }

    public static Supplier<IForgeRegistry<DataTrait>> DATA_TRAITS;

    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES;

    public static Supplier<IForgeRegistry<Skill>> SKILLS;

    @SubscribeEvent
    public static void onRegistryRegister(NewRegistryEvent event) {
        //Data trait registry.
        RegistryBuilder<DataTrait> traits = new RegistryBuilder<>();
        traits.setName(new ResourceLocation(Magus.MOD_ID, "data_traits"));
        DATA_TRAITS = event.create(traits);


        //Skill categories
        RegistryBuilder<SkillCategory> categories = new RegistryBuilder<>();
        categories.setName(new ResourceLocation(Magus.MOD_ID, "skill_categories"));
        SKILL_CATEGORIES = event.create(categories);

        //Skills
        RegistryBuilder<Skill> skills = new RegistryBuilder<>();
        skills.setName(new ResourceLocation(Magus.MOD_ID, "skills"));
        SKILLS = event.create(skills);

        //Forms

        //Modifiers
    }

    //What to do in the case of missing registry entries for each type of registry.
    @SubscribeEvent
    public void onMissing(MissingMappingsEvent event) {
        //Data Traits

        //Skill Categories

        //Skills

        //Forms

        //Modifiers
    }
}
