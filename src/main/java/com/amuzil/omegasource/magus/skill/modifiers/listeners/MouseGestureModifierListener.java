package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MousePointInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseShapeInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseVircle;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.MouseGestureModifierData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MouseGestureModifierListener extends ModifierListener<TickEvent> {
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    public List<String> mouseGestures;
    public MouseVircle vircle;
    private HashMap<MouseShapeInput, Integer> shapeCounter;
    private final RadixTree.InputType type;

    public MouseGestureModifierListener() {
        this(RadixTree.InputType.MOUSE_MOTION);
    }

    public MouseGestureModifierListener(RadixTree.InputType type) {
        this.modifierData = new MouseGestureModifierData();
        this.type = type;
        this.mouseGestures = new LinkedList<>();
    }

    @Override
    public void register(Runnable onSuccess) {
        super.register(onSuccess);
        MinecraftForge.EVENT_BUS.addListener(clientTickListener);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
    }

    @Override
    public void setupListener(CompoundTag compoundTag) {
        this.clientTickListener = event -> {
            if (Magus.keyboardMouseInputModule.keyPressed(Minecraft.getInstance().options.keyShift.getKey().getValue())) {
                Minecraft mci = Minecraft.getInstance();
                assert mci.player != null;
                double x = mci.mouseHandler.xpos();
                double y = mci.mouseHandler.ypos();
                Vec3 lookAngle = mci.player.getLookAngle();
                if (vircle == null) {
                    vircle = new MouseVircle(new MousePointInput(x, y, lookAngle));
                } else {
                    vircle.track(new MousePointInput(x, y, lookAngle));
                    if (vircle.hasMotionDirection()) {
                        mouseGestures.add(vircle.popMotionDirection());
                    }
                }
            }
        };
    }

    @Override
    public boolean shouldCollectModifierData(TickEvent event) {
        if (!Magus.keyboardMouseInputModule.keyPressed(Minecraft.getInstance().options.keyShift.getKey().getValue())) {
            if (!mouseGestures.isEmpty()) {
                mouseGestures.clear();
                return false;
            }
        }
        return true;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent event) {
        MouseGestureModifierData data = new MouseGestureModifierData(mouseGestures);
        this.mouseGestures = new ArrayList<>();
        return data;
    }

    @Override
    public MouseGestureModifierListener copy() {
        return new MouseGestureModifierListener();
    }
}
