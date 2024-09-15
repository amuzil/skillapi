package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardInputModule;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.List;
import java.util.function.Consumer;

public class KeyHeldModifierListener extends ModifierListener<TickEvent> {
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int currentHolding;
    private boolean isHeld = true;
    private boolean wasHeld = true;

    public KeyHeldModifierListener() {
        this.modifierData = new HeldModifierData();
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
        List<InputData> formConditions = FormDataRegistry.getInputsForForm(formToModify);

        int keyToHold = ((KeyHoldCondition) new ConditionBuilder().fromInputData(formConditions.get(formConditions.size() - 1)).build()).getKey();

        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START) {
                if (((KeyboardInputModule)Magus.keyboardInputModule).keyPressed(keyToHold)) {
                    this.isHeld = true;
                    this.currentHolding++;
                } else {
                    if(this.isHeld) {
                        this.wasHeld = true;
                        this.isHeld = false;
                    }
                }
            }
        };
    }

    @Override
    public boolean shouldCollectModifierData(TickEvent event) {
        if(isHeld && currentHolding > 0) {
            return true;
        }
        //so that we send a packet to say we've stopped holding(for continuous cast ability support)
        if(!this.isHeld && this.wasHeld) {
            this.wasHeld = false;
            Magus.keyboardInputModule.resetLastActivated();
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
    public ModifierListener copy() {
        return new KeyHeldModifierListener();
    }
}
