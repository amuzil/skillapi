package com.amuzil.omegasource.magus.registry;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillActive;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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

    public static Supplier<IForgeRegistry<DataTrait>> DATA_TRAITS;
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES;
    public static Supplier<IForgeRegistry<Skill>> SKILLS;

    public static void init() {
    }

    //How registering will work:
    /*
        All skills will be registered first, along with skill categories.
        Then, each skill will have its getTraits() method called, and each of its traits will be registered.
     */
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
    public static void onMissing(MissingMappingsEvent event) {
        //Data Traits

        //Skill Categories

        //Skills

        //Forms

        //Modifiers
    }

    @SubscribeEvent
    public static void gameRegistry(RegisterEvent event) {
        /* Skill Categories. */
        if (event.getRegistryKey().equals(SKILL_CATEGORIES.get().getRegistryKey())) {
            IForgeRegistry<SkillCategory> registry = SKILL_CATEGORIES.get();
            ResourceKey<Registry<SkillCategory>> resKey = registry.getRegistryKey();


            event.register(resKey, helper -> {
            });
        }

        /* Skills. */
        if (event.getRegistryKey().equals(SKILLS.get().getRegistryKey())) {
            IForgeRegistry<Skill> registry = SKILLS.get();
            ResourceKey<Registry<Skill>> resKey = registry.getRegistryKey();


            event.register(resKey, helper -> {
                Skill fireball = new SkillActive("fireball", null);
                registry.register(fireball.getId(), fireball);
            });
        }

        /* Data Traits. */
        if (event.getRegistryKey().equals(DATA_TRAITS.get().getRegistryKey())) {
            IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
            ResourceKey<Registry<DataTrait>> resKey = registry.getRegistryKey();


            /* For each skill registered, grab its list of SkillTraits and register it here. */
            event.register(resKey, helper -> {
            });
        }
    }
}