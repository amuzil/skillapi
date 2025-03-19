package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.forms.ExecuteFormPacket;
import com.amuzil.omegasource.magus.network.packets.forms.ReleaseFormPacket;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultInputModule {
    private final Consumer<InputEvent.Key> keyboardListener;
    private final Consumer<InputEvent.MouseButton> mouseListener;

    private boolean isHoldingShift = false;
    private boolean isHoldingControl = false;
    private boolean isHoldingAlt = false;
    private Form currentForm = Forms.NULL;
    private boolean isBending = true; // todo toggle system
    protected final List<Form> activeForms = Collections.synchronizedList(new LinkedList<>());

    public DefaultInputModule() {
        this.keyboardListener = keyboardEvent -> {
            int keyPressed = keyboardEvent.getKey();
            // NOTE: Minecraft's InputEvent.Key can only listen to the action InputConstants.REPEAT of one key at a time
            // tldr: it only fires the repeat event for the last key

            switch (keyboardEvent.getAction()) {
                case InputConstants.PRESS -> {
                    switch(keyPressed) {
                        case InputConstants.KEY_LSHIFT -> isHoldingShift = true;
                        case InputConstants.KEY_LCONTROL -> isHoldingControl = true;
                        case InputConstants.KEY_LALT -> isHoldingAlt = true;
                        default -> CheckFormsExecute(keyPressed);
                    }
                }
                case InputConstants.RELEASE -> {
                    switch(keyPressed) {
                        case InputConstants.KEY_LSHIFT -> isHoldingShift = false;
                        case InputConstants.KEY_LCONTROL -> isHoldingControl = false;
                        case InputConstants.KEY_LALT -> isHoldingAlt = false;
                        default -> CheckFormsRelease(keyPressed);
                    }
                }
            }
        };

        this.mouseListener = mouseEvent -> {
            int keyPressed = mouseEvent.getButton();
            switch (mouseEvent.getAction()) {
                case InputConstants.PRESS -> {
                    switch(keyPressed) {
                        default -> CheckFormsExecute(keyPressed);
                    }
                }
                case InputConstants.RELEASE -> {
                    switch(keyPressed) {
                        default -> CheckFormsRelease(keyPressed);
                    }
                }
            }
        };
    }

    private void CheckFormsExecute(int keyPressed) {
        if(isBending && currentForm == Forms.NULL) {
            if(!(isHoldingShift || isHoldingAlt || isHoldingControl)) {
                switch(keyPressed) {
                    case InputConstants.MOUSE_BUTTON_LEFT -> ExecuteForm(Forms.STRIKE);
                    case InputConstants.MOUSE_BUTTON_RIGHT -> ExecuteForm(Forms.BLOCK);
                }
            }
            else if(isHoldingShift) {
                switch(keyPressed) {
                    case InputConstants.KEY_W -> ExecuteForm(Forms.PUSH);
                    case InputConstants.KEY_S -> ExecuteForm(Forms.PULL);
                    case InputConstants.KEY_A -> ExecuteForm(Forms.LEFT);
                    case InputConstants.KEY_D -> ExecuteForm(Forms.RIGHT);
                    case InputConstants.KEY_Q -> ExecuteForm(Forms.LOWER);
                    case InputConstants.KEY_E -> ExecuteForm(Forms.RAISE);
                    case InputConstants.KEY_R -> ExecuteForm(Forms.ROTATE);
                }
            }
        }
    }

    private void CheckFormsRelease(int keyPressed) {
        switch(keyPressed) {
            case InputConstants.MOUSE_BUTTON_LEFT -> ReleaseForm(Forms.STRIKE);
            case InputConstants.MOUSE_BUTTON_RIGHT -> ReleaseForm(Forms.BLOCK);
            case InputConstants.KEY_W -> ReleaseForm(Forms.PUSH);
            case InputConstants.KEY_S -> ReleaseForm(Forms.PULL);
            case InputConstants.KEY_A -> ReleaseForm(Forms.LEFT);
            case InputConstants.KEY_D -> ReleaseForm(Forms.RIGHT);
            case InputConstants.KEY_Q -> ReleaseForm(Forms.LOWER);
            case InputConstants.KEY_E -> ReleaseForm(Forms.RAISE);
            case InputConstants.KEY_R -> ReleaseForm(Forms.ROTATE);
        }

    }

    private void ExecuteForm(Form form) {
        // send form execute packet
        MagusNetwork.sendToServer(new ExecuteFormPacket(form));
        activeForms.add(form);
        System.out.println("activeForms Path:" + activeForms);
        // track current form executing
        currentForm = form;
    }

    private void ReleaseForm(Form form) {
        if(currentForm != null && currentForm != Forms.NULL && currentForm.name().equals(form.name())) {
            // send form release packet
            MagusNetwork.sendToServer(new ReleaseFormPacket(currentForm));
            activeForms.remove(form);
            // reset current form executing
            currentForm = Forms.NULL;
        }
    }

    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.Key.class, keyboardListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, InputEvent.MouseButton.class, mouseListener);
    }

    public void unRegisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(keyboardListener);
        MinecraftForge.EVENT_BUS.unregister(mouseListener);
    }

    public void terminate() {
        unRegisterInputs();
        activeForms.clear();
    }
}
