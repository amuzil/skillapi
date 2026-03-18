package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.level.event.FormActivatedEvent;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.input.FormActivatedCondition;
import com.amuzil.omegasource.magus.skill.forms.Form;

public class SkillPathBuilder extends PathBuilder {
    /**
     *
     * @param form
     * @param release true = on release, false = on press
     * @return
     */
    public static Condition toCondition(Form form, boolean release) {
        return new FormActivatedCondition(FormActivatedEvent.class, form, release);
    }
}
