package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.List;

public class MouseDataBuilder {

    public static MouseWheelInput createWheelInput(Direction direction) {
        return createWheelInput(direction, -1, -1, -1);
    }

    public static MouseWheelInput createWheelInput(Direction direction, int duration) {
        return createWheelInput(direction, -1, duration, -1);
    }

    public static MouseWheelInput createWheelInput(Direction direction, int duration, int timeout) {
        return createWheelInput(direction, -1, duration, timeout);
    }

    public static MouseWheelInput createWheelInput(Direction direction, float totalChange, int duration, int timeout) {
        return new MouseWheelInput(direction, totalChange, duration, timeout);
    }

    public static MouseWheelInput createWheelInput(Direction direction, float totalChange, int timeout) {
        return createWheelInput(direction, totalChange, -1, timeout);
    }

    public static MouseWheelInput createWheelInput(Direction direction, float totalChange) {
        return createWheelInput(direction, totalChange, -1);
    }

    public static MouseMotionInput createSegmentMouseInput(MousePointInput... inputs) {
        return new MouseMotionInput(List.of(inputs));
    }

    public static MouseShapeInput createChainedMouseInput(MouseMotionInput... inputs) {
        return new MouseShapeInput(new LinkedList<>(List.of(inputs)));
    }

    // Utility to create a line from a start to an end with linear interpolation
    public static MouseMotionInput createLine(MousePointInput start, MousePointInput end, int numPoints) {
        List<MousePointInput> points = new LinkedList<>();
        for (int i = 0; i <= numPoints; i++) {
            double t = i / (double) numPoints;
            double x = (1 - t) * start.x() + t * end.x();
            double y = (1 - t) * start.y() + t * end.y();
            if (Minecraft.getInstance().player != null)
                points.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        return new MouseMotionInput(points);
    }

    // Utility to create a polygon from a list of vertices
    public static MouseShapeInput createPolygon(List<MousePointInput> vertices) {
        List<MouseMotionInput> segments = new LinkedList<>();
        for (int i = 0; i < vertices.size(); i++) {
            MousePointInput start = vertices.get(i);
            MousePointInput end = vertices.get((i + 1) % vertices.size()); // Wrap around to close the shape
            segments.add(createLine(start, end, 10)); // Adjust numPoints as needed
        }
        return new MouseShapeInput(segments);
    }

    // Create a polygon with interpolated points between vertices based on distanceBetweenPoints
    public static MouseShapeInput createPolygonWithInterpolation(List<MousePointInput> vertices, double distanceBetweenPoints) {
        List<MouseMotionInput> segments = new LinkedList<>();

        for (int i = 0; i < vertices.size(); i++) {
            MousePointInput start = vertices.get(i);
            MousePointInput end = vertices.get((i + 1) % vertices.size()); // Wrap around to close the shape

            // Calculate distance between start and end
            double dx = end.x() - start.x();
            double dy = end.y() - start.y();
            double segmentLength = Math.hypot(dx, dy);

            // Calculate number of points based on distanceBetweenPoints
            int numPoints = Math.max(2, (int) (segmentLength / distanceBetweenPoints));

            // Create line segment with interpolated points
            segments.add(createLine(start, end, numPoints));
        }
        return new MouseShapeInput(segments);
    }

    /**
     * Number corresponds to the MC mouse wheel direction.
     */
    public enum Direction {

        // Away from the user
        FORWARDS(1), // Towards the user
        BACK(-1), NEUTRAL(0);

        private final int dir;

        Direction(int dir) {
            this.dir = dir;
        }

        public int getDirection() {
            return this.dir;
        }
    }
}
