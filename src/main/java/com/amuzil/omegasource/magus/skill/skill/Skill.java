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
public class Skill {
    private ResourceLocation id;
    private SkillCategory category;
    private List<SkillTrait> traits;
    private List<SkillType> types;

    public Skill(String name, SkillCategory category) {
        this.id = new ResourceLocation(Magus.MOD_ID, name);
        this.category = category;
        Registries.SKILLS.get().register(id, this);
    }

    public Skill(ResourceLocation id, SkillCategory category) {

    }

    public SkillCategory getCategory() {
        return category;
    }

    public ResourceLocation getId() {
        return id;
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

    public boolean shouldStart(LivingEntity entity, RadixTree tree) {
        return false;
    }

    public boolean shouldRun(LivingEntity entity, RadixTree tree) {
        return false;
    }

    public boolean shouldStop(LivingEntity entity, RadixTree tree) {
        return false;
    }

    public void start(LivingEntity entity, RadixTree tree) {
    }

    public void run(LivingEntity entity, RadixTree tree) {
    }

    public void stop(LivingEntity entity, RadixTree tree) {
    }

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
