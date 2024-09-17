package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.List;

public class MouseDataBuilder {

    /**
     * Number corresponds to the MC mouse wheel direction.
     */
    public enum Direction {

        // Away from the user
        FORWARDS(1),
        // Towards the user
        BACK(-1),
        NEUTRAL(0);

        private final int dir;
        Direction(int dir) {
            this.dir = dir;
        }

        public int getDirection() {
            return this.dir;
        }
    }

    public static MouseWheelInput createWheelInput(Direction direction, int time) {
        return new MouseWheelInput(direction, time);
    }

    public static SegmentMouseInput createSegmentMouseInput(PointMouseInput... inputs) {
        return new SegmentMouseInput(List.of(inputs));
    }

    public static ShapeMouseInput createChainedMouseInput(SegmentMouseInput... inputs) {
        return new ShapeMouseInput(new LinkedList<>(List.of(inputs)));
    }

    // Utility to create a line from a start to an end with linear interpolation
    public static SegmentMouseInput createLine(PointMouseInput start, PointMouseInput end, int numPoints) {
        List<PointMouseInput> points = new LinkedList<>();
        for (int i = 0; i <= numPoints; i++) {
            double t = i / (double) numPoints;
            double x = (1 - t) * start.x() + t * end.x();
            double y = (1 - t) * start.y() + t * end.y();
            if(Minecraft.getInstance().player != null)
                points.add(new PointMouseInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        return new SegmentMouseInput(points);
    }

    // Utility to create a polygon from a list of vertices
    public static ShapeMouseInput createPolygon(List<PointMouseInput> vertices) {
        List<SegmentMouseInput> segments = new LinkedList<>();
        for (int i = 0; i < vertices.size(); i++) {
            PointMouseInput start = vertices.get(i);
            PointMouseInput end = vertices.get((i + 1) % vertices.size()); // Wrap around to close the shape
            segments.add(createLine(start, end, 10)); // Adjust numPoints as needed
        }
        return new ShapeMouseInput(segments);
    }

    // Create a polygon with interpolated points between vertices based on distanceBetweenPoints
    public static ShapeMouseInput createPolygonWithInterpolation(List<PointMouseInput> vertices, double distanceBetweenPoints) {
        List<SegmentMouseInput> segments = new LinkedList<>();

        for (int i = 0; i < vertices.size(); i++) {
            PointMouseInput start = vertices.get(i);
            PointMouseInput end = vertices.get((i + 1) % vertices.size()); // Wrap around to close the shape

            // Calculate distance between start and end
            double dx = end.x() - start.x();
            double dy = end.y() - start.y();
            double segmentLength = Math.hypot(dx, dy);

            // Calculate number of points based on distanceBetweenPoints
            int numPoints = Math.max(2, (int) (segmentLength / distanceBetweenPoints));

            // Create line segment with interpolated points
            segments.add(createLine(start, end, numPoints));
        }
        return new ShapeMouseInput(segments);
    }
}
