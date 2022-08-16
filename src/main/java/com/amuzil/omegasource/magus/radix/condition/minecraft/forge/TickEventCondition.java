package com.amuzil.omegasource.magus.radix.condition.minecraft.forge;

import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.function.Consumer;

//Success upon hitting the given time, but has a fail condition
public class TickEventCondition extends TickTimedEventCondition {

    private final Consumer<TickEvent> timeListener;
    private final Condition failCondition;
    //Max duration before it becomes true
    private final int max;

    //Fail condition lets this condition know whether to run onExpire or onSuccess upon the condition being completed.
    //Completion conditions are ors, not ands.
    public TickEventCondition(TickEvent.Type type, TickEvent.Phase phase, Condition subCondition,
                              boolean respectSubExpire, Condition failCondition, int timeout, int max) {
        super(type, phase, subCondition, respectSubExpire, timeout);
        this.max = max;
        this.failCondition = failCondition;

        this.timeListener = tickEvent -> {
            if (tickEvent.type == type && tickEvent.phase == phase) {
                if (this.current >= this.max)
                    this.onSuccess.run();
            }
        };
    }

    @Override
    public void register(Runnable onSuccess, Runnable onExpire) {
        super.register(onSuccess, onExpire);
        //No success registered upon time completion yet, therefore, no need to unregister anything.
        this.failCondition.register(this.onExpire, this.onSuccess);
        MinecraftForge.EVENT_BUS.addListener(timeListener);
    }

    @Override
    public void unregister() {
        super.unregister();
        MinecraftForge.EVENT_BUS.unregister(timeListener);
        failCondition.unregister();
    }
}
