package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Function;

public abstract class EventCondition<E extends Event> extends Condition {

    //Make sure to instantiate this to something in inherited classes.
    public Function<E, Boolean> condition = null;

    //Annotation needed for forge
    @SubscribeEvent
    public void listen(E event) {
        if (condition.apply(event)) {
            onSuccess.run();
        } else {
            onExpire.run();
        }
    }
}
