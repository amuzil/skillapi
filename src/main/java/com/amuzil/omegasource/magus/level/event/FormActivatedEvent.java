package com.amuzil.omegasource.magus.level.event;

import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

import java.util.LinkedList;
import java.util.List;


public class FormActivatedEvent extends Event {
    private final Form form;
    private final LivingEntity entity;
    private List<ModifierData> modifiers;
    private boolean released;

    public FormActivatedEvent(Form form, LivingEntity entity) {
        this.form = form;
        this.entity = entity;
        this.modifiers = new LinkedList<>();
        this.released = false;
    }

    public FormActivatedEvent(Form form, LivingEntity entity, boolean released) {
        this.form = form;
        this.entity = entity;
        this.modifiers = new LinkedList<>();
        this.released = released;
    }

    public FormActivatedEvent(Form form, List<ModifierData> modifierData, LivingEntity entity) {
        this(form, entity);
        this.modifiers = modifierData;
    }

    public Form getForm() {
        return this.form;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public List<ModifierData> getModifierData() {
        return this.modifiers;
    }

    public boolean released() {
        return this.released;
    }
}
