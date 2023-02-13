package com.amuzil.omegasource.magus.skill.util.bending;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.elements.Element;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BendingMaterialUtil {
    public static final TagKey<Block> WATERBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Magus.MOD_ID, "waterbending_material"));
    public static final TagKey<Block> EARTHBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Magus.MOD_ID, "earthbending_material"));
    public static List<TagKey<Block>> getBendableMaterialsForElement(Element element) {
        List<TagKey<Block>> toReturn = new ArrayList<>();

        switch (element.name()) {
            case "air":
                //add nothing
                break;
            case "water":
                toReturn.add(WATERBENDING_MATERIAL);
                break;
            case "earth":
                toReturn.add(EARTHBENDING_MATERIAL);
                break;
            case "fire":
                //add fire block?
                break;
            default: return toReturn;
        }
        return toReturn;
    }
}
