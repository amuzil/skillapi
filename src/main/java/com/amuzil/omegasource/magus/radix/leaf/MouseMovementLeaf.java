package com.amuzil.omegasource.magus.radix.leaf;

import com.amuzil.omegasource.magus.radix.Leaf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MouseMovementLeaf implements Leaf<List<Vec3>> {
    private final Consumer<LivingEvent.LivingTickEvent> listener;

    private final List<Vec3> path;

    public MouseMovementLeaf() {
        this.path = new LinkedList<>();
        this.listener = event -> {
            // TODO see if we can directly capture the mouse movement
            // This would be done by injecting a callback into net.minecraft.client.MouseHandler#onMove
            path.add(event.getEntity().getLookAngle());
        };
        MinecraftForge.EVENT_BUS.addListener(this.listener);
    }

    @Override
    public void burn() {
        this.path.clear();
        MinecraftForge.EVENT_BUS.unregister(this.listener);
    }

    @Override
    public void reset() {
        path.clear();
    }

    @Override
    public List<Vec3> measure() {
        return Collections.unmodifiableList(path);
    }
}
