package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Basic skill class. All other skills extend this.
 */
public abstract class Skill {
    private ResourceLocation id;
    private SkillCategory category;
    private List<SkillTrait> traits;
    private List<SkillType> types;

    public Skill(String name, SkillCategory category) {
        this.id = new ResourceLocation(Magus.MOD_ID, name);
        this.category = category;
    }

    public Skill(ResourceLocation id, SkillCategory category) {
        this.id = id;
        this.category = category;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<SkillTrait> getTraits() {
        return this.traits;
    }

    public List<SkillType> getTypes() {
        return types;
    }

    public final void lifecycle(LivingEntity entity, RadixTree tree) {
        // Run this asynchronously
        if (!shouldStart(entity, tree))
            return;

//        if (EventBus.post(new SkillLifecycleEvent.Start(entity, tree, this)) == EventResult.CANCEL)
//            return;
//
//        start(entity, tree);
//
//        while (shouldRun(entity, tree)) {
//            if (shouldStop(entity, tree)) {
//                if (EventBus.post(new SkillLifecycleEvent.Stop(entity, tree, this)) == EventResult.CANCEL)
//                    break;
//
//                stop(entity, tree);
//            } else {
//                if (EventBus.post(new SkillLifecycleEvent.Run(entity, tree, this)) == EventResult.CANCEL)
//                    break;
//                run(entity, tree);
//            }
//        }
    }

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
        OFFENSIVE,
        DEFENSIVE,
        MOBILITY,
        BUFF,
        UTILITY,
        RANGED,
        MELEE,
        CONSTRUCT
    }
}
