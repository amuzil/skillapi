package com.amuzil.omegasource.skillapi.data.conditions;

import com.amuzil.omegasource.skillapi.data.Condition;

public interface StateCondition {//extends Condition {

    boolean isSatisfied();

    default void register(Runnable success, Runnable expire) {
        if (isSatisfied()) {
            success.run();
        } else {
            expire.run();
        }
    }

}
