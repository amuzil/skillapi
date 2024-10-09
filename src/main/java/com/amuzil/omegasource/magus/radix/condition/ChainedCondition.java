package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;

import java.util.List;

public class ChainedCondition extends Condition {

    private final List<Condition> conditionSequence;
    private int currentConditionIndex = 0;
    private Condition currentCondition = null;

    private Runnable onPartialSuccess;
    private Runnable onPartialFailure;

    public ChainedCondition(List<Condition> conditionSequence) {
        this.conditionSequence = conditionSequence;
        this.onPartialSuccess = this::finishCurrentCondition;
        this.onPartialFailure = this::reset;
    }

    public ChainedCondition(Condition condition) {
        this(List.of(condition));
    }

    private void finishCurrentCondition() {
        if(currentCondition == null) return;
        if (currentConditionIndex == (conditionSequence.size() - 1)) {
            onSuccess.run();
            reset();
            return;
        } else {
            currentConditionIndex++;
        }

        currentCondition.unregister();

        currentCondition = conditionSequence.get(currentConditionIndex);
        if(currentConditionIndex == conditionSequence.size() - 1) {
            currentCondition.register(currentCondition.name(), onSuccess, () -> {
                onPartialFailure.run();
                onFailure.run();
            });
        } else {
            currentCondition.register(currentCondition.name(), onPartialSuccess, onPartialFailure);
        }

    }

    public void reset() {
        for (Condition condition : conditionSequence) {
            condition.reset();
            condition.unregister();
        }
        currentConditionIndex = 0;
        currentCondition.unregister();
        currentCondition = conditionSequence.get(currentConditionIndex);
        currentCondition.register(currentCondition.name(), onPartialSuccess, onPartialFailure);
    }

    @Override
    public void register(String name, Runnable onSuccess, Runnable onFailure) {
        super.register(name, onSuccess, onFailure);
    }

    public void partialRegister(Runnable onPartialSuccess, Runnable onPartialFailure) {
        this.onPartialSuccess = onPartialSuccess;
        this.onPartialFailure = onPartialFailure;
    }

    @Override
    public void register() {
        super.register();
        currentCondition = conditionSequence.get(currentConditionIndex);
        currentCondition.register(currentCondition.name(), onPartialSuccess, onPartialFailure);
        // Divorce register(runnable, runnable) from the regular register method.
        // This method should add requisite listeners to the forge event bus.
        // The other method should just adjust runnables as needed.
        currentCondition.register();
    }

    @Override
    public void unregister() {
        if (currentCondition != null)
            currentCondition.unregister();
    }
}
