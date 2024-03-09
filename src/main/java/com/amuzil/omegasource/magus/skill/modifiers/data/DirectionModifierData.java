package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectionModifierData extends BaseModifierData {

    private List<Direction> directions;

    public DirectionModifierData() {
        this.directions = new ArrayList<>();
    }

    public DirectionModifierData(List<Direction> directions) {
        this.directions = directions;
    }

    @Override
    public String getName() {
        return "DirectionModifier";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();
        ListTag listTag = new ListTag();

        this.directions.forEach(direction -> listTag.add(StringTag.valueOf(direction.name())));
        compoundTag.put("directions", listTag);

        return compoundTag;
    }

    @Override
    public DirectionModifierData copy() {
        return new DirectionModifierData();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.directions = new ArrayList<>();

        ListTag listTag = (ListTag) compoundTag.get("directions");
        listTag.forEach(tag -> this.directions.add(Direction.byName(tag.getAsString())));
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        DirectionModifierData directionModifierData = (DirectionModifierData) modifierData;
        this.directions.addAll(directionModifierData.directions);
    }

    @Override
    public void reset() {
        this.directions = new ArrayList<>();
    }

    @Override
    public void print() {
        LogManager.getLogger().info("MultiModifierData directions: \n" + this.directions.size());
//        LogManager.getLogger().info("MultiModifierData directions: \n" + this.directions.stream().map(direction -> direction.name()).collect(Collectors.joining(",\n")));
    }
}
