package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.MultiModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;

@OnlyIn(Dist.CLIENT)
public class MultiModifierListener extends ModifierListener<TickEvent.ClientTickEvent> {
    private int activationCounter;
    private String formName;

    public MultiModifierListener() {
        this.modifierData = new MultiModifierData();
        this.formName = "";
        this.activationCounter = 0;
    }

    @Override
    public void register(Runnable onSuccess) {
        super.register(onSuccess);
    }

    @Override
    public void unregister() {
        super.unregister();
    }

    public InputModule getTypedModule() {
        return Magus.keyboardMouseInputModule;
    }

    @Override
    public void setupListener(CompoundTag compoundTag) {
    }

    @Override
    public boolean shouldCollectModifierData(TickEvent.ClientTickEvent event) {
        InputModule module = getTypedModule();
        if (module.getActiveForm() != null) formName = module.getActiveForm().name();
        this.activationCounter = ((KeyboardMouseInputModule) module).getActivationCounter();
        if (!formName.equals("") && module.getActiveForm() != null && !formName.equals(module.getActiveForm().name())) {
            this.activationCounter = 0;
            return true;
        }
        return this.activationCounter > 0 && module.getActiveForm() != null && module.getLastActivatedForm() != null;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent.ClientTickEvent event) {
        MultiModifierData data = new MultiModifierData(activationCounter, formName);
        LogManager.getLogger().debug("Multi Count: " + activationCounter);
        this.activationCounter = 0;
        return data;
    }

    @Override
    public MultiModifierListener copy() {
        return new MultiModifierListener();
    }
}
