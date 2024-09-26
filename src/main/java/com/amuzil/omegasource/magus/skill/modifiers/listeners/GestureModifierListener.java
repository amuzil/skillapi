package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.ChainedKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.MultiKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseMotionInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MousePointInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseShapeInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseVircle;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.GestureModifierData;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GestureModifierListener extends ModifierListener<TickEvent> {
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    public List<String> mouseGestures;
    public MouseVircle vircle;
    private HashMap<MouseShapeInput, Integer> shapeCounter;
    private final RadixTree.InputType type;

    public GestureModifierListener() {
        this(RadixTree.InputType.MOUSE_MOTION);
    }

    public GestureModifierListener(RadixTree.InputType type) {
        this.modifierData = new GestureModifierData();
        this.type = type;
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
        Form formToModify = FormDataRegistry.getFormByName(compoundTag.getString("lastFormActivated"));
        List<InputData> formInputs = FormDataRegistry.getInputsForForm(formToModify, type);

        InputData lastInput = formInputs.get(formInputs.size() - 1);

        this.clientTickListener = event -> {
            if (Magus.keyboardInputModule.keyPressed(Minecraft.getInstance().options.keyShift.getKey().getValue())) {
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
        if (!Magus.keyboardInputModule.keyPressed(Minecraft.getInstance().options.keyShift.getKey().getValue())) {
            if (!mouseGestures.isEmpty()) {
                mouseGestures.clear();
                return false;
            }
        }
        return true;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent event) {
        GestureModifierData data = new GestureModifierData(mouseGestures);
        this.mouseGestures = new ArrayList<>();
        return data;
    }

    @Override
    public GestureModifierListener copy() {
        return new GestureModifierListener();
    }
}
