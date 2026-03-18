package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class TargetModifierData extends BaseModifierData {

    private ArrayList<Vec3> targetPositions;

    public TargetModifierData() {
        this.targetPositions = new ArrayList<>();
    }

    public TargetModifierData(ArrayList<Vec3> directions) {
        this.targetPositions = directions;
    }

    @Override
    public String getName() {
        return "TargetModifier";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();
        ListTag listTag = new ListTag();

        this.targetPositions.forEach(target -> {
            CompoundTag targetPosition = new CompoundTag();
            targetPosition.putDouble("x", target.x);
            targetPosition.putDouble("y", target.y);
            targetPosition.putDouble("z", target.z);
            listTag.add(targetPosition);
        });
        compoundTag.put("targetPositions", listTag);

        return compoundTag;
    }

    @Override
    public TargetModifierData copy() {
        return new TargetModifierData();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.targetPositions = new ArrayList<>();

        ListTag listTag = (ListTag) compoundTag.get("targetPositions");
        listTag.forEach(tag -> {
            CompoundTag target = (CompoundTag)tag;
            double x = target.getDouble("x");
            double y = target.getDouble("y");
            double z = target.getDouble("z");
            this.targetPositions.add(new Vec3(x, y, z));
        });
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        TargetModifierData targetModifierData = (TargetModifierData) modifierData;
        this.targetPositions.addAll(targetModifierData.targetPositions);
    }

    @Override
    public void reset() {
        this.targetPositions = new ArrayList<>();
    }

    @Override
    public void print() {
        LogManager.getLogger().info("TargetModifierData targetPositions: \n" + this.targetPositions.size());
    }
}
