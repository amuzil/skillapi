package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.level.event.FormActivatedEvent;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.input.FormActivatedCondition;
import com.amuzil.omegasource.magus.skill.forms.Form;

public class SkillPathBuilder extends PathBuilder {
    public Condition toCondition(Form form) {
        return new FormActivatedCondition(FormActivatedEvent.class, form);
    }
}
