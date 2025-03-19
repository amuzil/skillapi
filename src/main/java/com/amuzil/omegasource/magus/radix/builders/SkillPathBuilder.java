package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.level.event.FormActivatedEvent;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.input.FormActivatedCondition;
import com.amuzil.omegasource.magus.skill.forms.Form;

public class SkillPathBuilder extends PathBuilder {
    public static Condition toCondition(Form form) {
        // TODO:
        //  Change to Executed and Release events, have a boolean here to determine that then create the event
        //  that relies on Aidan's packets
        return new FormActivatedCondition(FormActivatedEvent.class, form);
    }
}
