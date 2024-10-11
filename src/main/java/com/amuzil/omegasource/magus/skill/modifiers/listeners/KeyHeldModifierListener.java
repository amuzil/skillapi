package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.ChainedKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.MultiKeyInput;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyHeldModifierListener extends ModifierListener<TickEvent> {
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int currentHolding;
    private boolean isHeld = true;
    private boolean wasHeld = true;
    private RadixTree.InputType type;

    public KeyHeldModifierListener() {
        this(RadixTree.InputType.KEYBOARD_MOUSE);
    }

    public KeyHeldModifierListener(RadixTree.InputType type) {
        this.modifierData = new HeldModifierData();
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
        List<Integer> keyCodes = new ArrayList<>();
        if (lastInput instanceof ChainedKeyInput) {
            keyCodes.add(((ChainedKeyInput) lastInput).trueLast().key().getValue());
        }
        /// I need to fix multi :(
        else if (lastInput instanceof MultiKeyInput) {
            for (KeyInput key: ((MultiKeyInput) lastInput).keys())
                keyCodes.add(key.key().getValue());
        }
        else {
            // If it's registered to the keyboard mouse input module, it's going to be some variant
            // of KeyInput.
            keyCodes.add(((KeyInput) lastInput).key().getValue());
        }

        InputModule  module = Magus.keyboardMouseInputModule;
//        if (Objects.requireNonNull(type) == RadixTree.InputType.MOUSE) {
//            module = Magus.mouseInputModule;
//        } else {
//            module = Magus.keyboardInputModule;
//        }

        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START) {
                boolean pressed = true;
                for (int key : keyCodes) {
                    if (!module.keyPressed(key)) {
                        pressed = false;
                        break;
                    }
                }
                if (pressed) {
                    this.isHeld = true;
                    this.currentHolding++;
                } else {
                    if (this.isHeld) {
                        this.wasHeld = true;
                        this.isHeld = false;
                    }
                }
            }
        };
    }

    @Override
    public boolean shouldCollectModifierData(TickEvent event) {
        if (isHeld && currentHolding > 0) {
            return true;
        }
        //so that we send a packet to say we've stopped holding(for continuous cast ability support)
        if (!this.isHeld && this.wasHeld) {
            this.wasHeld = false;
            Magus.keyboardMouseInputModule.resetLastActivated();
            return true;
        }
        return false;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent event) {
        HeldModifierData data = new HeldModifierData(currentHolding, isHeld);
        this.currentHolding = 0;
        return data;
    }

    @Override
    public KeyHeldModifierListener copy() {
        return new KeyHeldModifierListener();
    }
}
