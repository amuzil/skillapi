package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;
import net.minecraft.world.entity.ai.Brain;

import javax.xml.ws.Provider;

public class SimpleCondition extends Condition {

    Provider<Boolean> condition;

    public SimpleCondition(Provider<Boolean> condition) {
        this.condition = condition;
    }

    public SimpleCondition() {
        this.condition = null;
    }

    @Override
    //TODO: Fix this/check if using the right provider
    public void register(Runnable onSuccess, Runnable onExpire) {
        if (condition.invoke(true)) {
            onSuccess.run();
        } else {
            onExpire.run();
        }
    }

    @Override
    public void unregister() {
        //Expires anyway if the boolean condition is failed, but...
        this.onExpire.run();
    }
}
