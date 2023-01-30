package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;

import java.util.List;

public class ChainedCondition extends Condition {

    private final List<Condition> chainedConditions;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private int currentConditionIndex = 0;
    private Condition currentCondition = null;

    private final Runnable onPartialSuccess;

    public ChainedCondition(List<Condition> chainedConditions) {
        this.chainedConditions = chainedConditions;
        this.onPartialSuccess = this::finishCurrentCondition;
    }

    public void finishCurrentCondition() {
        if(currentCondition == null) return;
        currentCondition.unregister();
        if(currentConditionIndex == (chainedConditions.size() - 1)) {
            onCompleteSuccess.run();
            currentConditionIndex = 0;
        } else {
            currentConditionIndex++;
            currentCondition = chainedConditions.get(currentConditionIndex);
            currentCondition.register(onPartialSuccess, onCompleteFailure);
        }
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
        currentCondition = chainedConditions.get(currentConditionIndex);
        currentCondition.register(onPartialSuccess, () -> {
            // todo: if we dont want to completely fail the chain then this method can be expanded.
            onCompleteFailure.run();
        });
    }

    @Override
    public void unregister() {
        currentCondition.unregister();
    }
}
