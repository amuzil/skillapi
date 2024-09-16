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
        PointMouseInput centroid = start();
        Vector2d mouseDirection = getVector2d(mouseInputs);
        String direction = "";
        // Create the vector buckets
        double degree_span = 45.0D;
        Vector2d up = new Vector2d(centroid.x(), centroid.y());
        Vector2d down = new Vector2d(centroid.x(), -centroid.y());
        Vector2d left = new Vector2d(-centroid.x(), centroid.y());
        Vector2d right = new Vector2d(centroid.x(), centroid.y());

        if (getAngle2D(up, mouseDirection) <= degree_span) {
            direction = "up";
        } else if (getAngle2D(down, mouseDirection) <= degree_span) {
            direction = "down";
        } else if (getAngle2D(right, mouseDirection) <= degree_span) {
            direction = "right";
        } else if (getAngle2D(left, mouseDirection) <= degree_span) {
            direction = "left";
        }
        return direction;
    }

    public Vector2d getVector2d(List<PointMouseInput> mouseInputs) {
        PointMouseInput centroid = start();
        PointMouseInput last = start();
        int radius = 250;
        Vector2d v1 = new Vector2d(centroid.x(), centroid.y());
        Vector2d v2 = new Vector2d(last.x(), last.y());
        return v2.sub(v1).normalize();
    }

    // Get angle between two 2D vectors and return in ° (degrees)
    public static double getAngle2D(Vector2d v1, Vector2d v2) {
        double dotProduct = v1.mul((1), (0)).dot(v2.mul((1), (0)));
        double radians = dotProduct / (getMagnitude2D(v1) * getMagnitude2D(v2));
        return Math.toDegrees(Math.acos(radians));
    }

    // Get magnitude/length of 2D vector v
    public static double getMagnitude2D(Vector2d v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
    }
}
