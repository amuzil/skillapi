package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MultiKeyCondition extends Condition {
    private static final int TIMEOUT_IN_TICKS = 15;
    private final List<Condition> concurrentConditions;
    private Dictionary<Integer, Boolean> conditionsMet;
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private int executionTime = 0;
    private boolean startedExecuting = false;

    public MultiKeyCondition(List<Condition> concurrentConditions) {
        this.concurrentConditions = concurrentConditions;
        this.registerEntry();
    }

    public MultiKeyCondition(Condition condition) {
        this(List.of(condition));
    }

    private void checkConditionMet() {
        for (Iterator<Boolean> it = conditionsMet.elements().asIterator(); it.hasNext(); ) {
            // none/not all conditions have been met yet, exit loop and dont execute.
            if (!it.next()) return;
        }
        this.onSuccess.run();
        this.reset();
    }

    public List<Condition> getSubConditions() {
        return concurrentConditions;
    }

    @Override
    public void register(String name, Runnable onSuccess, Runnable onFailure) {
        super.register(name, onSuccess, onFailure);
        this.clientTickListener = event -> {
            if (event.phase == TickEvent.ClientTickEvent.Phase.START
            && Minecraft.getInstance().getOverlay() == null) {
//                System.out.println("Ticking.");
                if (startedExecuting) {
                    System.out.println("Started Executing.");
                    executionTime++;
                    if (executionTime > TIMEOUT_IN_TICKS) {
                        this.onFailure().run();

                        LogManager.getLogger().info("MULTI CONDITION TIMED OUT");
                        this.reset();
                    }
                }
            }
        };
        this.reset();
    }

    public void reset() {
        AtomicInteger counter = new AtomicInteger();
        conditionsMet = new Hashtable<>();
        this.startedExecuting = false;
        this.executionTime = 0;
        concurrentConditions.forEach(condition -> {
            int id = counter.getAndIncrement();
            condition.register(condition.name(), () -> {
                System.out.println("First Condition Met: " + condition);
                synchronized (conditionsMet) {
                    // Debugging statement:
//                    LogManager.getLogger().info("MARKING CONDITION MET: " + concurrentConditions.get(id).getClass());
                    startedExecuting = true;
                    conditionsMet.put(id, true);
                    condition.unregister();
                    condition.reset();
                }
                checkConditionMet();
            }, () -> {
                synchronized (conditionsMet) {
                    conditionsMet.put(id, false);
                    condition.unregister();
                    condition.reset();
                }
                checkConditionMet();
            });
            conditionsMet.put(id, false);
        });
    }

    @Override
    public void register() {
        super.register();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, clientTickListener);
        for (Condition condition : getSubConditions())
            condition.register();
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
        concurrentConditions.forEach(Condition::unregister);
    }

    @Override
    public int hashCode() {
        int hashSum = 0;
        for (Condition condition : concurrentConditions)
            hashSum += condition.hashCode();
        return Objects.hash(name, hashSum);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof MultiKeyCondition other)) {
            return false;
        } else {
//            System.out.println("this: stored in tree -> " + this);
//            System.out.println("other: activeCondition from user input -> " + other);
            return Objects.equals(concurrentConditions.size(), ((MultiKeyCondition) obj).concurrentConditions.size())
                    &&
                    /* Makes sure an alternative key condition that's been pressed has been pressed at least as long
                     * as the currently compared condition. */
                    obj.hashCode() == this.hashCode();
        }
    }


    //TODO: Hashing, equals function like in KeyHoldCondition
}