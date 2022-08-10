package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;

//import javax.xml.ws.Provider;

public class SimpleCondition extends Condition {

    boolean condition;

    public SimpleCondition(boolean condition) {
        this.condition = condition;
    }

    public SimpleCondition() {
        this.condition = false;
    }

    @Override
    //TODO: Fix this/check if using the right provider
    public void register(Runnable onSuccess, Runnable onExpire) {
        if (condition) {
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
