package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.magus.skill.util.data.SkillData;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.List;

/**
 * Basic skill class. All other skills extend this.
 */
public abstract class Skill {
    private final ResourceLocation id;
    private final SkillCategory category;
    // How the skill was activated. Useful if you want different methods to influence the skill in different ways.
    // For complex, game-design move combinations, see ModifierData for how to alter your skills.
    protected RadixTree.ActivationType activatedType;
    private SkillData skillData;
    private RadixTree requirements;

    public Skill(String modID, String name, SkillCategory category) {
        this(new ResourceLocation(modID, name), category);
    }

    public Skill(String name, SkillCategory category) {
        this(Magus.MOD_ID, name, category);
    }

    public Skill(ResourceLocation id, SkillCategory category) {
        this.id = id;
        this.category = category;
        // Menu is default
        this.activatedType = RadixTree.ActivationType.MENU;
    }

    public RadixTree.ActivationType getActivatedType() {
        return this.activatedType;
    }

    public List<RadixTree.ActivationType> getActivationTypes() {
        return this.skillData.getActivationTypes();
    }

    public void addActivationType(RadixTree.ActivationType type) {
        this.skillData.addActivationType(type);
    }


    public SkillCategory getCategory() {
        return category;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<SkillTrait> getTraits() {
        return this.skillData.getSkillTraits();
    }

    public List<SkillType> getTypes() {
        return this.skillData.getSkillTypes();
    }

    public void tick(LivingEntity entity, RadixTree tree) {
        //Run this asynchronously
        if (!shouldStart(entity, tree)) return;

        //Remember, for some reason post only returns true upon the event being cancelled. Blame Forge.
        if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Start(entity, tree, this))) return;

        start(entity, tree);

        while (shouldRun(entity, tree)) {
            if (shouldStop(entity, tree)) {
                if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Stop(entity, tree, this))) break;

                stop(entity, tree);
            } else {
                if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Run(entity, tree, this))) break;
                run(entity, tree);
            }
        }
    }


    public abstract HashMap<RadixTree.ActivationType, List<ConditionPath>> getActivationPaths();

    public abstract boolean shouldStart(LivingEntity entity, RadixTree tree);

    public abstract boolean shouldRun(LivingEntity entity, RadixTree tree);

    public abstract boolean shouldStop(LivingEntity entity, RadixTree tree);

    public abstract void start(LivingEntity entity, RadixTree tree);

    public abstract void run(LivingEntity entity, RadixTree tree);

    public abstract void stop(LivingEntity entity, RadixTree tree);

    /**
     * Different skill types. A skill can be multiple of one type.
     */
    public enum SkillType {
        OFFENSIVE, DEFENSIVE, MOBILITY, BUFF, UTILITY, RANGED, MELEE, CONSTRUCT
    }
}
