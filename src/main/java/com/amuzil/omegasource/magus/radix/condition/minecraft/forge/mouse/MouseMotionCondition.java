package com.amuzil.omegasource.magus.radix.condition.minecraft.forge.mouse;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseMotionShapeComparator;
import com.amuzil.omegasource.magus.skill.forms.Form;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MouseMotionCondition extends Condition {
    private final Consumer<TickEvent.ClientTickEvent> clientTickListener;
    private final MouseMotionShapeComparator shapeComparator;
    private final List<Point2D> mousePath;
    private boolean tracking;
    private final double speedThreshold;
    private final int key; // Key that needs to be held
    private final Form requiredForm; // Form that needs to be activated
    private final List<Form> activeForms; // Track active forms in real-time

    public MouseMotionCondition(double speedThreshold, double errorMargin, int key, Form requiredForm) {
        this.mousePath = new ArrayList<>();
        this.shapeComparator = new MouseMotionShapeComparator(errorMargin);
        this.speedThreshold = speedThreshold;
        this.key = key;
        this.requiredForm = requiredForm;
        this.activeForms = new ArrayList<>();
        this.tracking = false;

        this.clientTickListener = event -> {
            if (event.phase == TickEvent.Phase.START && Minecraft.getInstance().getOverlay() == null) {
                boolean keyPressed = key == -1 || isKeyPressed(key); // -1 indicates no key requirement
                boolean formActive = requiredForm == null || activeForms.contains(requiredForm);

                if (keyPressed && formActive) {
                    trackMouseMovement();
                }
            }
        };

        this.registerEntry();
    }

    private void trackMouseMovement() {
        double currentX = Minecraft.getInstance().mouseHandler.xpos();
        double currentY = Minecraft.getInstance().mouseHandler.ypos();

        // Minecraft coordinates: up is negative Y, down is positive Y, left is negative X, right is positive X
        // No adjustment needed as the coordinates naturally fit the system described

        if (!tracking) {
            mousePath.clear();
            tracking = true;
        }

        mousePath.add(new Point2D.Double(currentX, currentY));

        // Example shape matching, modify as needed
        if (shapeComparator.compareToPath(mousePath, generateDesiredShape())) {
            this.onSuccess.run();
            reset();
        }
    }

    public boolean isSatisfied() {
        return !mousePath.isEmpty();
    }

    public void reset() {
        this.mousePath.clear();
        this.tracking = false;
    }

    public void setActiveForms(List<Form> forms) {
        activeForms.clear();
        activeForms.addAll(forms);
    }

    @Override
    public void register(String name, Runnable onSuccess, Runnable onFailure) {
        super.register(name, onSuccess, onFailure);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, clientTickListener);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(clientTickListener);
    }

    @Override
    public String name() {
        return "mouse_motion";
    }

    private boolean isKeyPressed(int key) {
        return Minecraft.getInstance().options.keyMappings[key].isDown();
    }

    private List<Point2D> generateDesiredShape() {
        // Placeholder for generating shapes like lines, spirals, etc.
        return new ArrayList<>();
    }
}