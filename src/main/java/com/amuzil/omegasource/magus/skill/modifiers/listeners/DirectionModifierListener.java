package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.DirectionModifierData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.LinkedList;
import java.util.List;

import static com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseMotionInput.getAngle2D;


public class DirectionModifierListener extends ModifierListener<LivingEvent.LivingTickEvent> {
    private final double motionThreshold = 0.08d;

    public DirectionModifierListener() {
        this.modifierData = new DirectionModifierData();
    }

    //this ModifierListener doesn't use any nested listeners
    @Override
    public void setupListener(CompoundTag compoundTag) {
    }

    @Override
    public boolean shouldCollectModifierData(LivingEvent.LivingTickEvent event) {
        if(!(event.getEntity() instanceof Player)) return false;
        //            System.out.println(getDirection(event.getEntity().getLookAngle(), event.getEntity().getDeltaMovement().normalize()));
        //            LogManager.getLogger().info("MOVE MOTION: " + event.getEntity().getDeltaMovement().length());
        //            LogManager.getLogger().info(event.getEntity().getDeltaMovement());
        return event.getEntity().getDeltaMovement().length() > motionThreshold;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(LivingEvent.LivingTickEvent event) {
        List<String> directions = new LinkedList<>();
        String direction = getDirection(event.getEntity().getLookAngle(), event.getEntity().getDeltaMovement().normalize());
        if (!direction.isEmpty())
            directions.add(direction);
        return new DirectionModifierData(directions);
    }

    @Override
    public ModifierListener copy() {
        return new DirectionModifierListener();
    }

    public String getDirection(Vec3 faceDirection, Vec3 movementDirection) {
        String direction = "";
        double degree_span = 45.0D;
        Vec3 front = faceDirection.normalize();
        Vec3 back = new Vec3(-faceDirection.x, faceDirection.y, -faceDirection.z).normalize();
        Vec3 right = new Vec3(-faceDirection.z, faceDirection.y, faceDirection.x).normalize();
        Vec3 left = new Vec3(faceDirection.z, faceDirection.y, -faceDirection.x).normalize();

        if (movementDirection.y() >= 1.0D) {
            direction = "up";
        } else if (movementDirection.y() <= -1.0D) {
            direction = "down";
        } else if (getAngle2D(front, movementDirection) <= degree_span) {
            direction = "forward";
        } else if (getAngle2D(back, movementDirection) <= degree_span) {
            direction = "back";
        } else if (getAngle2D(right, movementDirection) <= degree_span) {
            direction = "right";
        } else if (getAngle2D(left, movementDirection) <= degree_span) {
            direction = "left";
        }
        return direction;
    }
}
