package com.amuzil.omegasource.magus.registry;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.elements.Element;
import com.amuzil.omegasource.magus.skill.elements.Elements;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import com.amuzil.omegasource.magus.skill.skill.SkillActive;
import com.amuzil.omegasource.magus.skill.skill.SkillCategory;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * All custom registries go here.
 */
@Mod.EventBusSubscriber(modid = Magus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registries {

    public static Supplier<IForgeRegistry<DataTrait>> DATA_TRAITS;
    public static Supplier<IForgeRegistry<SkillCategory>> SKILL_CATEGORIES;
    public static Supplier<IForgeRegistry<Skill>> SKILLS;
    public static Supplier<IForgeRegistry<Form>> FORMS;


    public static List<DataTrait> traits = new ArrayList<>();
    public static List<SkillCategory> categories = new ArrayList<>();
    public static List<Skill> skills = new ArrayList<>();
    public static List<Form> forms = new ArrayList<>();

    // SKILLS
    // this is a placeholder skill for testing purposes.
    public static final Skill FIREBALL = new SkillActive("fireball", null);


    public static void init() {
    }

    /**
     * Registry methods.
     */
    //These are added to the forge event bus registry. To get a specific trait, use the registry.
    public static void registerTraits(List<DataTrait> dataTraits) {
        traits.addAll(dataTraits);
    }

    public static void registerTrait(DataTrait dataTrait) {
        traits.add(dataTrait);
    }

    public static void registerSkillCategories(List<SkillCategory> skillCategories) {
        categories.addAll(skillCategories);
    }

    public static void registerSkillCategory(SkillCategory skillCategory) {
        categories.add(skillCategory);
    }


    public static void registerSkills(List<Skill> registrySkills) {
        skills.addAll(registrySkills);
    }

    public static void registerSkill(Skill registrySkill) {
        skills.add(registrySkill);
    }

    public static void registerForms(List<Form> registryForms) {
        forms.addAll(registryForms);
    }

    public static void registerForm(Form registryForm) {
        forms.add(registryForm);
    }

    public static void registerElement(Element element) {
        categories.add(element);
        Elements.ELEMENTS.add(element);
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
        RegistryBuilder<Form> forms = new RegistryBuilder<>();
        forms.setName(new ResourceLocation(Magus.MOD_ID, "forms"));
        FORMS = event.create(forms);

        //Modifiers
    }


    //What to do in the case of missing registry entries for each type of registry.

    @Mod.EventBusSubscriber(modid = Magus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeRegistries {
        @SubscribeEvent
        public static void onMissing(MissingMappingsEvent event) {
            //Data Traits

            //Skill Categories

            //Skills

            //Forms

            //Modifiers
        }
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
                registry.register(FIREBALL.getId(), FIREBALL);
            });
        }

        /* Forms. */
        if (event.getRegistryKey().equals(FORMS.get().getRegistryKey())) {
            IForgeRegistry<Form> registry = FORMS.get();
            ResourceKey<Registry<Form>> resKey = registry.getRegistryKey();


            event.register(resKey, helper -> {
                for (Form form : forms)
                    registry.register(form.name(), form);
                //TODO: Element specific forms
            });
        }

        /* Data Traits. */
        if (event.getRegistryKey().equals(DATA_TRAITS.get().getRegistryKey())) {
            IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
            ResourceKey<Registry<DataTrait>> resKey = registry.getRegistryKey();


            //Registers every Data Trait for every skill included within Magus.
            //Register other traits manually.
            registerTraitsFromSkills(SKILLS.get().getValues().stream().toList(), event);
            event.register(resKey, helper -> {
                for (DataTrait trait : traits)
                    registry.register(trait.getName(), trait);
            });
        }
    }

    /**
     * Use this method to register the data traits of all registered skills.
     *
     * @param skills List of skills.
     * @param event  Registry event.
     * @param modID  ModID.
     */
    public static void registerTraitsFromSkills(List<Skill> skills, RegisterEvent event,
                                                String modID) {
        ResourceKey<Registry<DataTrait>> key = DATA_TRAITS.get().getRegistryKey();
        IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
        for (Skill skill : skills)
            for (SkillTrait trait : skill.getTraits())
                event.register(key, helper ->
                        registry.register(new ResourceLocation(modID) + trait.getName(), trait));

    }

    /**
     * Same as the above method, but if you standardise your modID in your data,
     * then use this.
     *
     * @param skills Skills to register.
     * @param event  The registry event.
     */
    public static void registerTraitsFromSkills(List<Skill> skills, RegisterEvent event) {
        ResourceKey<Registry<DataTrait>> key = DATA_TRAITS.get().getRegistryKey();
        IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
        for (Skill skill : skills)
            for (SkillTrait trait : skill.getTraits())
                event.register(key, helper ->
                        registry.register(trait.getName(), trait));

    }
}
