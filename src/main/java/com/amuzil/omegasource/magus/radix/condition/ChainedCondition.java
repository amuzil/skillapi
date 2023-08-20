package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class ChainedCondition extends Condition {

    private final List<Condition> chainedConditions;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private int currentConditionIndex = 0;
    private Condition currentCondition = null;

    private final Runnable onPartialSuccess;
    private final Runnable onPartialFailure;

    public ChainedCondition(List<Condition> chainedConditions) {
        this.chainedConditions = chainedConditions;
        this.onPartialSuccess = this::finishCurrentCondition;
        this.onPartialFailure = this::reset;
    }

    private void finishCurrentCondition() {
        if(currentCondition == null) return;
        LogManager.getLogger().info("UNREGISTERING CURRENT CONDITION");
        currentCondition.unregister();
        if (currentConditionIndex == (chainedConditions.size() - 1)) {
            onCompleteSuccess.run();
            currentConditionIndex = 0;
        } else {
            currentConditionIndex++;
        }
        currentCondition = chainedConditions.get(currentConditionIndex);
        if(currentConditionIndex == chainedConditions.size() - 1) {
            currentCondition.register(onPartialSuccess, () -> {
                onPartialFailure.run();
                onCompleteFailure.run();
            });
        } else {
            currentCondition.register(onPartialSuccess, onPartialFailure);
        }

    }

    private void reset() {
        currentConditionIndex = 0;
        currentCondition.unregister();
        currentCondition = chainedConditions.get(currentConditionIndex);
        currentCondition.register(onPartialSuccess, onPartialFailure);
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
        currentCondition = chainedConditions.get(currentConditionIndex);
        currentCondition.register(onPartialSuccess, onPartialFailure);
    }

    @Override
    public void unregister() {
        currentCondition.unregister();
    }
}
