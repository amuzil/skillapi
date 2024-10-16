package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.radix.Condition;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.function.Consumer;


public class MultiClientCondition extends MultiCondition {
    private static final int TIMEOUT_IN_TICKS = 3;
    private Consumer<TickEvent.ClientTickEvent> clientTickListener;


    public MultiClientCondition(List<Condition> concurrentConditions) {
        super(concurrentConditions);
    }

    public MultiClientCondition(Condition condition) {
        super(condition);
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
//                    System.out.println("Started Executing.");
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

    @Override
    public void register() {
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
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("%s[ %s ]", this.getClass().getSimpleName(), concurrentConditions);
    }

}