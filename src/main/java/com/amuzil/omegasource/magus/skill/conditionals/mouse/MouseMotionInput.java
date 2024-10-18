package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record MouseMotionInput(List<MousePointInput> mouseInputs) implements InputData {

    public MousePointInput start() {
        return mouseInputs.get(0);
    }

    public MousePointInput end() {
        return mouseInputs().get(mouseInputs().size() - 1);
    }

    public Vec3 getAimDirection() {
        if (mouseInputs.size() > 1)
            return mouseInputs.get(mouseInputs.size() / 2).lookAngle();
        else
            return new Vec3((0),(1),(0));
    }

    public String getMotionDirection() {
        MousePointInput first = start();
        Vec3 mouseDirection = getMouseDirection();
        String direction = "";
        double degree_span = 90.0D; // Note: 45° doesn't cover the entire unit circle, leaving room to recognize diagonals
        Vec3 right = new Vec3(-first.lookAngle().z(), first.lookAngle().y(), first.lookAngle().x()).normalize();
        Vec3 left = new Vec3(first.lookAngle().z(), first.lookAngle().y(), -first.lookAngle().x()).normalize();

        if (mouseDirection.y() > 0.45D) {
            direction = "up";
        } else if (mouseDirection.y() < -0.45D) {
            direction = "down";
        } else if (getAngle2D(right, mouseDirection) <= degree_span) {
            direction = "right";
        } else if (getAngle2D(left, mouseDirection) <= degree_span) {
            direction = "left";
        }
        return direction;
    }

    public Vec3 getMouseDirection() {
        Vec3 first = start().lookAngle();
        Vec3 last = end().lookAngle();
        return last.subtract(first).normalize();
    }

    // Get angle between two 2D vectors and return in ° (degrees)
    public static double getAngle2D(Vec3 v1, Vec3 v2) {
        double dotProduct = v1.multiply((1), (0), (1)).dot(v2.multiply((1), (0), (1)));
        double radians = dotProduct / (getMagnitude2D(v1) * getMagnitude2D(v2));
        return Math.toDegrees(Math.acos(radians));
    }

    // Get magnitude/length of 2D vector v
    public static double getMagnitude2D(Vec3 v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.z, 2));
    }
}
