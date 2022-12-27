package com.amuzil.omegasource.magus.skill.skill;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.EventBus;

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

    public void tick(LivingEntity entity, RadixTree tree) {
        //Run this asynchronously
        if (!shouldStart(entity, tree))
            return;

        //Remember, for some reason post only returns true upon the event being cancelled. Blame Forge.
        if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Start(entity, tree, this)))
            return;

        start(entity, tree);

        while (shouldRun(entity, tree)) {
            if (shouldStop(entity, tree)) {
                if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Stop(entity, tree, this)))
                    break;

                stop(entity, tree);
            } else {
                if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Run(entity, tree, this)))
                    break;
                run(entity, tree);
            }
        }
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
