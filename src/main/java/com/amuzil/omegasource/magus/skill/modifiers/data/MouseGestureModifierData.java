package com.amuzil.omegasource.magus.skill.modifiers.data;

import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseShapeInput;
import com.amuzil.omegasource.magus.skill.modifiers.api.BaseModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MouseGestureModifierData extends BaseModifierData {

    private List<String> mouseGestures;
    private HashMap<MouseShapeInput, Integer> shapeCounter;

    public MouseGestureModifierData() {
        this.mouseGestures = new ArrayList<>();
        this.shapeCounter = new HashMap<>();
    }

    public MouseGestureModifierData(List<String> gestures) {
        this.mouseGestures = gestures;
    }

    @Override
    public String getName() {
        return "GestureModifier";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = super.serializeNBT();
        ListTag listTag = new ListTag();

        this.mouseGestures.forEach(gesture -> {
            CompoundTag mouseGestures = new CompoundTag();
            mouseGestures.putString("gesture", gesture);
            listTag.add(mouseGestures);
        });
        compoundTag.put("mouseGestures", listTag);

        return compoundTag;
    }

    @Override
    public MouseGestureModifierData copy() {
        return new MouseGestureModifierData();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.mouseGestures = new ArrayList<>();

        ListTag listTag = (ListTag) compoundTag.get("mouseGestures");
        assert listTag != null;
        listTag.forEach(mouseGesture -> {
            CompoundTag gesture = (CompoundTag) mouseGesture;
            this.mouseGestures.add(gesture.getString("gesture"));
        });
    }

    //it is safe to cast at this point because the public add(ModifierData data) method encapsulates type-checking
    @Override
    protected void mergeFields(ModifierData modifierData) {
        MouseGestureModifierData mouseGestureModifierData = (MouseGestureModifierData) modifierData;
        this.mouseGestures.addAll(mouseGestureModifierData.mouseGestures);
    }

    @Override
    public void reset() {
        this.mouseGestures = new ArrayList<>();
        this.shapeCounter = new HashMap<>();
    }

    @Override
    public void print() {
        LogManager.getLogger().info("GestureModifier mouseGestures: " + mouseGestures);
    }
}
