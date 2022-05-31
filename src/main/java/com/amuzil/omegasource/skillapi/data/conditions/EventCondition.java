package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;
import net.minecraftforge.eventbus.api.Event;

public abstract class EventCondition<E extends Event> implements Condition {

    //These are only public for package organisation. DO NOT TOUCH OUTSIDE YOUR CONDITIONS.
    //Success moves to the next node down in the tree.
    public Runnable success;
    //Expires the tree, making the listener disregard further nodes and reset back to the top.
    public Runnable expire;

    public abstract void listen(E event);

    @Override
    public void register(Runnable success, Runnable expire) {
        this.success = success;
        this.expire = expire;
    }
}
