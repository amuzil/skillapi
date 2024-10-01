package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;

import java.util.List;

public class ChainedCondition extends Condition {

    private final List<Condition> conditionSequence;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private int currentConditionIndex = 0;
    private Condition currentCondition = null;

    private final Runnable onPartialSuccess;
    private final Runnable onPartialFailure;

    public ChainedCondition(List<Condition> conditionSequence) {
        this.conditionSequence = conditionSequence;
        this.onPartialSuccess = this::finishCurrentCondition;
        this.onPartialFailure = this::reset;
        this.registerEntry();
    }

    public ChainedCondition(Condition condition) {
        this(List.of(condition));
    }

    private void finishCurrentCondition() {
        if(currentCondition == null) return;
        if (currentConditionIndex == (conditionSequence.size() - 1)) {
            onCompleteSuccess.run();
            reset();
            return;
        } else {
            currentConditionIndex++;
        }
        //Debugging statement
      //  LogManager.getLogger().info("UNREGISTERING CURRENT CONDITION: " + currentCondition.getClass());

        currentCondition.unregister();

        currentCondition = conditionSequence.get(currentConditionIndex);
        if(currentConditionIndex == conditionSequence.size() - 1) {
            currentCondition.register(currentCondition.name(), onCompleteSuccess, () -> {
                onPartialFailure.run();
                onCompleteFailure.run();
            });
        } else {
            currentCondition.register(currentCondition.name(), onPartialSuccess, onPartialFailure);
        }

    }

    public void reset() {
        currentConditionIndex = 0;
        currentCondition.unregister();
        currentCondition = conditionSequence.get(currentConditionIndex);
        currentCondition.register(currentCondition.name(), onPartialSuccess, onPartialFailure);
    }

    @Override
    public void register(String name, Runnable onSuccess, Runnable onFailure) {
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
    }

    @Override
    public void register() {
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
