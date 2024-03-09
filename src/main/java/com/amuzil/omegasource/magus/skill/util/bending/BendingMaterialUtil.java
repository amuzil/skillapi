package com.amuzil.omegasource.magus.skill.util.bending;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.elements.Discipline;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BendingMaterialUtil {
    public static final TagKey<Block> AIRBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Magus.MOD_ID, "airbending_material.json"));
    public static final TagKey<Block> WATERBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Magus.MOD_ID, "waterbending_material"));
    public static final TagKey<Block> EARTHBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Magus.MOD_ID, "earthbending_material"));
    public static final TagKey<Block> FIREBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Magus.MOD_ID, "firebending_material"));
    public static List<TagKey<Block>> getBendableMaterialsForElement(Discipline discipline) {
        List<TagKey<Block>> toReturn = new ArrayList<>();

        switch (discipline.name()) {
            case "air":
                //add nothing
                toReturn.add(AIRBENDING_MATERIAL);
                break;
            case "water":
                toReturn.add(WATERBENDING_MATERIAL);
                break;
            case "earth":
                toReturn.add(EARTHBENDING_MATERIAL);
                break;
            case "fire":
                toReturn.add(FIREBENDING_MATERIAL);
                //add fire block?
                break;
            default: return toReturn;
        }
        return toReturn;
    }
}
