package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.key.ChainedKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.MultiKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseMotionInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class KeyHeldModifierListener extends ModifierListener<TickEvent.ClientTickEvent> {
    private final RadixTree.InputType type;
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int currentHolding;
    private boolean isHeld = true;
    private boolean wasHeld = true;
    private List<Integer> activeKeyCodes;

    public KeyHeldModifierListener() {
        this(RadixTree.InputType.KEYBOARD_MOUSE);
    }

    public KeyHeldModifierListener(RadixTree.InputType type) {
        this.modifierData = new HeldModifierData();
        this.type = type;
        this.activeKeyCodes = new LinkedList<>();
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

    public List<Integer> getKeyCodes(Form form, RadixTree.InputType type) {
        List<InputData> formInputs = FormDataRegistry.getInputsForForm(form, type);
        List<Integer> keyCodes = new ArrayList<>();

        if (formInputs != null) {
            InputData lastInput = formInputs.get(formInputs.size() - 1);
            if (lastInput instanceof ChainedKeyInput) {
                for (KeyInput key : ((ChainedKeyInput) lastInput).last().keys())
                    keyCodes.add(key.key().getValue());
            } else if (lastInput instanceof MultiKeyInput) {
                for (KeyInput key : ((MultiKeyInput) lastInput).keys())
                    keyCodes.add(key.key().getValue());
            } else if (lastInput instanceof MouseWheelInput || lastInput instanceof MouseMotionInput) {
                // Ignore these since they're not keys!
            } else {
                // If it's registered to the keyboard mouse input module, it's going to be some variant of KeyInput.
                keyCodes.add(((KeyInput) lastInput).key().getValue());
            }
        }
        return keyCodes;
    }

    public InputModule getTypedModule(RadixTree.InputType type) {
        return Magus.keyboardMouseInputModule;
    }

    @Override
    public void setupListener(CompoundTag compoundTag) {
        // TODO: FIx this
        InputModule module = Magus.keyboardMouseInputModule;

        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START) {
                boolean pressed = true;
                // If all requisite keys aren't pressed, don't iterate modifier data
                for (int key : activeKeyCodes) {
                    if (!module.keyPressed(key)) {
                        pressed = false;
                        break;
                    }
                }
                if (activeKeyCodes.isEmpty())
                    pressed = false;

                if (pressed) {
                    LogManager.getLogger().debug("KEY HELD FOR: " + currentHolding);
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
    public boolean shouldCollectModifierData(TickEvent.ClientTickEvent event) {
        InputModule module = getTypedModule(type);
        if (module.getActiveForm() != null && (module.getLastActivatedForm() == null ||
                !module.getActiveForm().name().equals(module.getLastActivatedForm().name()))) {
            activeKeyCodes = getKeyCodes(module.getActiveForm(), type);
        }

        if (activeKeyCodes.isEmpty()) return false;

        if (isHeld && currentHolding > 0 && currentHolding % 3 == 0) {
            return true;
        }
        //so that we send a packet to say we've stopped holding(for continuous cast ability support)
        if (!this.isHeld && this.wasHeld) {
            this.wasHeld = false;
            this.currentHolding = 0;
            // Forcibly collects data jic
//            Magus.keyboardMouseInputModule.queueModifierData(collectModifierDataFromEvent(event));
//            Magus.keyboardMouseInputModule.resetLastActivated();
            return true;
        }
        return false;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent.ClientTickEvent event) {
        LogManager.getLogger().debug("Collected Held Data at: " + currentHolding);
        HeldModifierData data = new HeldModifierData(currentHolding, isHeld);
        return data;
    }

    @Override
    public KeyHeldModifierListener copy() {
        return new KeyHeldModifierListener();
    }
}
