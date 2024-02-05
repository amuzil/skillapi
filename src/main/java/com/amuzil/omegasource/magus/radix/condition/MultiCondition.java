package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiCondition extends Condition {
    private final List<Condition> concurrentConditions;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private Dictionary<Integer, Boolean> conditionsMet;

    public MultiCondition(List<Condition> concurrentConditions) {
        this.concurrentConditions = concurrentConditions;
    }

    public MultiCondition(Condition condition) {
        this.concurrentConditions = List.of(condition);
    }

    private void checkConditionMet() {
        for (Iterator<Boolean> it = conditionsMet.elements().asIterator(); it.hasNext(); ) {
            // none/not all conditions have been met yet, exit loop and dont execute.
            if (it.next() == false) return;
        }
        this.onCompleteSuccess.run();
        this.reset();
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
        this.reset();
    }

    public void reset() {
        AtomicInteger counter = new AtomicInteger();
        conditionsMet = new Hashtable<>();
        concurrentConditions.forEach(condition -> {
            int id = counter.getAndIncrement();
            condition.register(() -> {
                synchronized (conditionsMet){
//                    LogManager.getLogger().info("MARKING CONDITION MET: " + concurrentConditions.get(id).getClass());
                    conditionsMet.put(id, true);
                    condition.unregister();
                }
                checkConditionMet();
            }, onCompleteFailure);
            conditionsMet.put(id, false);
        });
    }

    @Override
    public void unregister() {
        concurrentConditions.forEach(Condition::unregister);
    }
}