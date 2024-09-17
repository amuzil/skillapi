package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;

import java.util.List;

public record SegmentMouseInput(List<PointMouseInput> mouseInputs) {

    public PointMouseInput start() {
        return mouseInputs.get(0);
    }

    public PointMouseInput end() {
        return mouseInputs().get(mouseInputs().size() - 1);
    }

    public String getDirection() {
        // TODO - Test this logic
        PointMouseInput first = start();
        PointMouseInput last = end();
        Vec3 mouseDirection = getMouseDirection();
        String direction = "";
        // Create the vector buckets
        double degree_span = 45.0D; // Make 45 degrees to include diagonal directions
        // Cardinal directions
        Vec3 right = new Vec3(-first.lookAngle().z(), first.lookAngle().y(), first.lookAngle().x());
        Vec3 left = new Vec3(first.lookAngle().z(), first.lookAngle().y(), -first.lookAngle().x());
        System.out.println("mouseDirection: " + String.format("x: %.2f, z: %.2f", mouseDirection.x(), mouseDirection.z()));
        System.out.println("getAngle between right and mouseDirection: " + getAngle2D(right, mouseDirection));
        // TODO - Fix right and left motion recognition
        if (mouseDirection.y() > 0.7D) {
            direction = "up";
        } else if (mouseDirection.y() < -0.7D) {
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
//        System.out.println("lookAngle 1: " + first.lookAngle());
//        System.out.println("lookAngle 2: " + last.lookAngle());
//        Vector2d v1 = new Vector2d(first.lookAngle().x(), first.lookAngle().z());
//        Vector2d v2 = new Vector2d(last.lookAngle().x(), last.lookAngle().z());
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
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
    }
}
