package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MultiCondition extends Condition {
    private final List<Condition> concurrentConditions;
    private Runnable onCompleteSuccess;
    private Runnable onCompleteFailure;
    private Dictionary<Integer, Boolean> conditionsMet;
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private static int TIMEOUT_IN_TICKS = 15;
    private int executionTime = 0;
    private boolean startedExecuting = false;

    public MultiCondition(List<Condition> concurrentConditions) {
        this.concurrentConditions = concurrentConditions;
    }

    public MultiCondition(Condition condition) {
        this.concurrentConditions = List.of(condition);
    }

    private void checkConditionMet() {
        for (Iterator<Boolean> it = conditionsMet.elements().asIterator(); it.hasNext(); ) {
            // none/not all conditions have been met yet, exit loop and dont execute.
            if (!it.next()) return;
        }
        this.onCompleteSuccess.run();
        this.reset();
    }

    @Override
    public void register(Runnable onSuccess, Runnable onFailure) {
        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START) {
                if(startedExecuting)
                {
                    executionTime++;
                    if(executionTime > TIMEOUT_IN_TICKS) {
                        this.onCompleteFailure.run();

                        LogManager.getLogger().info("MULTI CONDITION TIMED OUT");
                        this.reset();
                    }
                }
            }
        };
        this.onCompleteSuccess = onSuccess;
        this.onCompleteFailure = onFailure;
        this.reset();
    }

    public void reset() {
        AtomicInteger counter = new AtomicInteger();
        conditionsMet = new Hashtable<>();
        this.startedExecuting = false;
        this.executionTime = 0;
        concurrentConditions.forEach(condition -> {
            int id = counter.getAndIncrement();
            condition.register(() -> {
                synchronized (conditionsMet) {
                    // Debugging statement:
//                    LogManager.getLogger().info("MARKING CONDITION MET: " + concurrentConditions.get(id).getClass());
                    startedExecuting = true;
                    conditionsMet.put(id, true);
                    condition.unregister();
                }
                checkConditionMet();
            }, () -> {
                synchronized (conditionsMet) {
                    conditionsMet.put(id, false);
                    condition.unregister();
                }
                checkConditionMet();
            });
            conditionsMet.put(id, false);
        });
    }

    @Override
    public void register() {
        this.reset();
    }

    @Override
    public void unregister() {
        concurrentConditions.forEach(Condition::unregister);
    }

    @Override
    public String name() {
        return "multi_condition";
    }
}