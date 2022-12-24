package com.amuzil.omegasource.magus.registry;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.traits.DataTraits;
import com.amuzil.omegasource.magus.skill.util.traits.IDataTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * All custom registries go here.
 */
@Mod.EventBusSubscriber(modid = Magus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registries {

    public static void init() {
    }

    @ObjectHolder(registryName = "data_traits", value = "magus:data_traits")
    public static ForgeRegistry<IDataTrait> DATA_TRAITS;

    @ObjectHolder(registryName = "skill_categories", value = "magus:skill_categories")
    public static ForgeRegistry<IDataTrait> SKILL_CATEGORIES;

    @ObjectHolder(registryName = "skills", value = "magus:skills")
    public static ForgeRegistry<IDataTrait> SKILLS;

    @SubscribeEvent
    public static void onRegistryRegister(NewRegistryEvent event) {
        //Data trait registry.
        RegistryBuilder<IDataTrait> traits = new RegistryBuilder<>();
        traits.setName(new ResourceLocation(Magus.MOD_ID, "data_traits"));
        event.create(traits);

        RegistryBuilder<SkillCategory> categories = new RegistryBuilder<>();
        categories.setName(new ResourceLocation(Magus.MOD_ID, "skill_categories"));
        event.create(categories);

        RegistryBuilder<Skill> skills = new RegistryBuilder<>();
        skills.setName(new ResourceLocation(Magus.MOD_ID, "skills"));
        event.create(skills);
    }
}
