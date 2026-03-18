package com.amuzil.omegasource.magus.radix.condition.input;

import com.amuzil.omegasource.magus.level.event.FormActivatedEvent;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class FormActivatedCondition extends EventCondition<FormActivatedEvent> {

    private Form form;
    private List<ModifierData> modifierData;
    public FormActivatedCondition(Class<FormActivatedEvent> eventType, Function<FormActivatedEvent, Boolean> condition) {
        super(eventType, condition);
    }

    public FormActivatedCondition(Class<FormActivatedEvent> eventType, Function<FormActivatedEvent, Boolean> condition, Form form,
                                  List<ModifierData> data) {
        super(eventType, condition);
        this.form = form;
        this.modifierData = data;
    }

    public FormActivatedCondition(Class<FormActivatedEvent> eventType, Form form) {
        this(eventType, formActivatedEvent -> formActivatedEvent.getForm().name().equals(form.name()), form, new LinkedList<>());
    }

    public FormActivatedCondition(Class<FormActivatedEvent> eventType, Form form, boolean released) {
        this(eventType, formActivatedEvent -> formActivatedEvent.getForm().name().equals(form.name()) && formActivatedEvent.released() == released,
                form, new LinkedList<>());
    }

    public FormActivatedCondition(Class<FormActivatedEvent> eventType, Form form, List<ModifierData> data) {
        this(eventType, formActivatedEvent -> formActivatedEvent.getForm().name().equals(form.name()), form, data);
    }

    public List<ModifierData> getModifierData() {
        return this.modifierData;
    }
    public Form getForm() {
        return this.form;
    }

    // TODO: Need to set this class' modifier data to modifier data from the event
    @Override
    public Runnable onSuccess() {

        return super.onSuccess();
    }
}
