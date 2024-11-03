package com.amuzil.omegasource.magus.level.event;

import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class FormActivatedEvent extends Event {
    private Form form;
    private List<ModifierData> modifiers;

    public FormActivatedEvent(Form form) {
        this.form = form;
    }

    public FormActivatedEvent(Form form, List<ModifierData> modifierData) {
        this(form);
        this.modifiers = modifierData;
    }
    public Form getForm() {
        return this.form;
    }

    public List<ModifierData> getModifierData() {
        return this.modifiers;
    }
}
