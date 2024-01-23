package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class SequenceCondition extends Condition {

    private final List<Condition> conditionSequence;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private int currentConditionIndex = 0;
    private Condition currentCondition = null;

    private final Runnable onPartialSuccess;
    private final Runnable onPartialFailure;

    public SequenceCondition(List<Condition> conditionSequence) {
        this.conditionSequence = conditionSequence;
        this.onPartialSuccess = this::finishCurrentCondition;
        this.onPartialFailure = this::reset;
    }

    private void finishCurrentCondition() {
        if(currentCondition == null) return;
        LogManager.getLogger().info("UNREGISTERING CURRENT CONDITION");
        currentCondition.unregister();
        if (currentConditionIndex == (conditionSequence.size() - 1)) {
            onCompleteSuccess.run();
            currentConditionIndex = 0;
        } else {
            currentConditionIndex++;
        }
        currentCondition = conditionSequence.get(currentConditionIndex);
        if(currentConditionIndex == conditionSequence.size() - 1) {
            currentCondition.register(onCompleteSuccess, () -> {
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
        currentCondition = conditionSequence.get(currentConditionIndex);
        currentCondition.register(onPartialSuccess, onPartialFailure);
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
        currentCondition = conditionSequence.get(currentConditionIndex);
        currentCondition.register(onPartialSuccess, onPartialFailure);
    }

    @Override
    public void unregister() {
        currentCondition.unregister();
    }
}
