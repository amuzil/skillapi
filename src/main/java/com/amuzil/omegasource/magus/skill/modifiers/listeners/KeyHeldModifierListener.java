package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.HeldModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class KeyHeldModifierListener extends ModifierListener<TickEvent.ClientTickEvent> {
    private final RadixTree.InputType type;
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int currentHolding;
    private boolean isHeld = true;
    private boolean wasHeld = true;
    private String formName;
    private List<Integer> activeKeyCodes;

    public KeyHeldModifierListener() {
        this(RadixTree.InputType.KEYBOARD_MOUSE);
    }

    public KeyHeldModifierListener(RadixTree.InputType type) {
        this.modifierData = new HeldModifierData();
        this.type = type;
        this.activeKeyCodes = new LinkedList<>();
        this.formName = "";
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
//                System.out.println(formName);
                // If all requisite keys aren't pressed, don't iterate modifier data
                for (int key : activeKeyCodes) {
                    if (!module.keyPressed(key)) {
                        pressed = false;
                        break;
                    }
                }
                if (activeKeyCodes.isEmpty()) {
                    pressed = false;
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
    public boolean shouldCollectModifierData(TickEvent.ClientTickEvent event) {
        InputModule module = getTypedModule(type);
        if (module.getActiveForm() != null && !module.getActiveForm().name().equals("null")) {
            formName = module.getActiveForm().name();
            if (!KeyboardMouseInputModule.getKeyCodes(module.getActiveForm(), type).isEmpty()) {
                activeKeyCodes = KeyboardMouseInputModule.getKeyCodes(module.getActiveForm(), type);
                if (module.getLastActivatedForm() != null && !formName.equals(module.getLastActivatedForm().name())) {
                    currentHolding = 0;
                }
            }
        }

        if (activeKeyCodes.isEmpty()) return false;

        if (isHeld && currentHolding > 0) {
            return true;
        }
        //so that we send a packet to say we've stopped holding(for continuous cast ability support)
        if (!this.isHeld && this.wasHeld) {
            this.wasHeld = false;
            this.currentHolding = 0;
            LogManager.getLogger().debug("Form Name: " + module.getActiveForm().name());
            // Forcibly collects data jic
//            Magus.keyboardMouseInputModule.queueModifierData(collectModifierDataFromEvent(event));
//            Magus.keyboardMouseInputModule.resetLastActivated();
            return false;
        }
        return false;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(TickEvent.ClientTickEvent event) {
        LogManager.getLogger().debug("Collected Held Data at: " + currentHolding);
        HeldModifierData data = new HeldModifierData(currentHolding, isHeld, formName);
        return data;
    }

    @Override
    public KeyHeldModifierListener copy() {
        return new KeyHeldModifierListener();
    }
}
