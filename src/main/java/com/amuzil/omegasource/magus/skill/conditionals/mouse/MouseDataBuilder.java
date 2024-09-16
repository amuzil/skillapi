package com.amuzil.omegasource.magus.skill.conditionals.mouse;

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

    public static SegmentMouseInput createSegmentMouseInput(MouseMotionInput... inputs) {
        return new SegmentMouseInput(List.of(inputs));
    }

    public static ShapeMouseInput createChainedMouseInput(SegmentMouseInput... inputs) {
        return new ShapeMouseInput(new LinkedList<>(List.of(inputs)));
    }

    // Utility to create a line from a start to an end with linear interpolation
    public static SegmentMouseInput createLine(MouseMotionInput start, MouseMotionInput end, int numPoints) {
        List<MouseMotionInput> points = new LinkedList<>();
        for (int i = 0; i <= numPoints; i++) {
            double t = i / (double) numPoints;
            double x = (1 - t) * start.x() + t * end.x();
            double y = (1 - t) * start.y() + t * end.y();
            points.add(new MouseMotionInput(x, y));
        }
        return new SegmentMouseInput(points);
    }

    // Utility to create a polygon from a list of vertices
    public static ShapeMouseInput createPolygon(List<MouseMotionInput> vertices) {
        List<SegmentMouseInput> segments = new LinkedList<>();
        for (int i = 0; i < vertices.size(); i++) {
            MouseMotionInput start = vertices.get(i);
            MouseMotionInput end = vertices.get((i + 1) % vertices.size()); // Wrap around to close the shape
            segments.add(createLine(start, end, 10)); // Adjust numPoints as needed
        }
        return new ShapeMouseInput(segments);
    }
}
