package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class MultiCondition extends Condition {
    private final List<Condition> concurrentConditions;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private final Runnable onPartialSuccess;
    private int conditionsMet;

    public MultiCondition(List<Condition> concurrentConditions) {
        this.concurrentConditions = concurrentConditions;
        this.onPartialSuccess = this::conditionMet;

        for (Condition cond : concurrentConditions) {
            System.out.println("Multi " + this + "Condition: " + cond);
        }
    }

    public MultiCondition(Condition condition) {
        this.concurrentConditions = List.of(condition);
        this.onPartialSuccess = this::conditionMet;
    }

    private void conditionMet() {
        this.conditionsMet++;

        if(this.conditionsMet == concurrentConditions.size()) this.onCompleteSuccess.run();
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
        concurrentConditions.forEach(condition -> condition.register(onPartialSuccess, onCompleteFailure));
    }

    @Override
    public void unregister() {
        concurrentConditions.forEach(Condition::unregister);
    }
}



