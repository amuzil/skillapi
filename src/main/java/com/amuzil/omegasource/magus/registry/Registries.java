package com.amuzil.omegasource.magus.registry;

import com.amuzil.omegasource.magus.Magus;
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
                Skill fireball = new SkillActive("fireball", null);
                registry.register(fireball.getId(), fireball);
            });
        }

        /* Forms. */
        if (event.getRegistryKey().equals(FORMS.get().getRegistryKey())) {
            IForgeRegistry<Form> registry = FORMS.get();
            ResourceKey<Registry<Form>> resKey = registry.getRegistryKey();


            event.register(resKey, helper -> {
                Form push = new Form("push");
                registry.register(push.getName(), push);

                Form pull = new Form("pull");
                registry.register(pull.getName(), pull);

                Form raise = new Form("raise");
                registry.register(raise.getName(), raise);

                Form lower = new Form("lower");
                registry.register(lower.getName(), lower);

                Form burst = new Form("burst");
                registry.register(burst.getName(), burst);

                Form arc = new Form("arc");
                registry.register(arc.getName(), arc);

                Form compress = new Form("compress");
                registry.register(compress.getName(), compress);

                Form expand = new Form("expand");
                registry.register(expand.getName(), expand);

                Form twist = new Form("twist");
                registry.register(twist.getName(), twist);

                Form strike = new Form("strike");
                registry.register(strike.getName(), strike);

                Form block = new Form("block");
                registry.register(block.getName(), block);

                Form breathe = new Form("breathe");
                registry.register(breathe.getName(), breathe);

                Form step = new Form("step");
                registry.register(step.getName(), step);

                //TODO: Element specific forms
            });
        }

        /* Data Traits. */
        if (event.getRegistryKey().equals(DATA_TRAITS.get().getRegistryKey())) {
            IForgeRegistry<DataTrait> registry = DATA_TRAITS.get();
            ResourceKey<Registry<DataTrait>> resKey = registry.getRegistryKey();


            //Registers every Data Trait for every skill included within Magus.
            //Register other traits manually.
            registerTraitsFromSkills((List<Skill>) SKILLS.get().getValues(), event);
        }
    }

    /**
     * Use this method to register the data traits of all registered skills.
     * @param skills List of skills.
     * @param event Registry event.
     * @param modID ModID.
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
     *  Same as the above method, but if you standardise your modID in your data,
     *  then use this.
     * @param skills Skills to register.
     * @param event The registry event.
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
