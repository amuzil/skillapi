package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.util.ArrayList;
import java.util.List;

public class MousePathComparator {

    // Generate a circular shape centered at the player's mouse position with rotation and scaling
    public static ShapeMouseInput generateCircle(PointMouseInput center, double scale, double rotationDegrees, double distanceBetweenPoints) {
        List<PointMouseInput> points = new ArrayList<>();

        // Calculate the number of points based on circumference and desired distance between points
        double circumference = 2 * Math.PI * scale;
        int numPoints = Math.max(10, (int) (circumference / distanceBetweenPoints));

        // Convert rotation to radians
        double rotationRadians = Math.toRadians(rotationDegrees);

        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double x = center.x() + scale * Math.cos(angle + rotationRadians);
            double y = center.y() - scale * Math.sin(angle + rotationRadians); // Flip y-axis
            points.add(new PointMouseInput(x, y));
        }
        SegmentMouseInput segmentInput = new SegmentMouseInput(points);
        return new ShapeMouseInput(List.of(segmentInput));
    }

    // Generate a straight line starting from the player's mouse position with rotation and scaling
    public static ShapeMouseInput generateLine(PointMouseInput start, double scale, double rotationDegrees, double distanceBetweenPoints) {
        List<PointMouseInput> points = new ArrayList<>();

        // Calculate the number of points based on length and desired distance between points
        int numPoints = Math.max(2, (int) (scale / distanceBetweenPoints));

        // Convert rotation to radians
        double angleRadians = Math.toRadians(rotationDegrees);
        double endX = start.x() + scale * Math.cos(angleRadians);
        double endY = start.y() - scale * Math.sin(angleRadians); // Flip y-axis

        for (int i = 0; i <= numPoints; i++) {
            double t = i / (double) numPoints;
            double x = start.x() + t * (endX - start.x());
            double y = start.y() + t * (endY - start.y());
            points.add(new PointMouseInput(x, y));
        }
        SegmentMouseInput line = new SegmentMouseInput(points);
        return new ShapeMouseInput(List.of(line));
    }

    // Generate a polygon centered at the player's mouse position with rotation and scaling
    public static ShapeMouseInput generatePolygon(PointMouseInput center, int sides, double scale, double rotationDegrees, double distanceBetweenPoints, boolean isDiamond) {
        List<PointMouseInput> vertices = new ArrayList<>();

        double angleIncrement = 2 * Math.PI / sides;
        double rotationRadians = Math.toRadians(rotationDegrees);

        // For diamond, offset the rotation by 45 degrees
        if (isDiamond) {
            rotationRadians += Math.toRadians(45);
        }

        for (int i = 0; i < sides; i++) {
            double angle = angleIncrement * i + rotationRadians;
            double x = center.x() + scale * Math.cos(angle);
            double y = center.y() - scale * Math.sin(angle); // Flip y-axis
            vertices.add(new PointMouseInput(x, y));
        }

        // Interpolate points between vertices based on distanceBetweenPoints
        return MouseDataBuilder.createPolygonWithInterpolation(vertices, distanceBetweenPoints);
    }

    // Generate a star shape centered at the player's mouse position with rotation and scaling
    public static ShapeMouseInput generateStar(PointMouseInput center, int pointsCount, double scale, double rotationDegrees, double distanceBetweenPoints) {
        List<PointMouseInput> vertices = new ArrayList<>();
        double angleIncrement = Math.PI / pointsCount;
        double rotationRadians = Math.toRadians(rotationDegrees);

        // Define outer and inner radii based on scale
        double outerRadius = scale;
        double innerRadius = scale / 2; // Adjust the ratio as needed

        for (int i = 0; i < pointsCount * 2; i++) {
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double angle = angleIncrement * i + rotationRadians;
            double x = center.x() + radius * Math.cos(angle);
            double y = center.y() - radius * Math.sin(angle); // Flip y-axis
            vertices.add(new PointMouseInput(x, y));
        }

        // Interpolate points between vertices based on distanceBetweenPoints
        return MouseDataBuilder.createPolygonWithInterpolation(vertices, distanceBetweenPoints);
    }

    // Spiral!!!
    public static ShapeMouseInput generateSpiral(PointMouseInput center, int turns, double initialRadius, double finalRadius, double rotationDegrees, double distanceBetweenPoints) {
        List<PointMouseInput> points = new ArrayList<>();

        double rotationRadians = Math.toRadians(rotationDegrees);
        double totalAngle = 2 * Math.PI * turns;
        double radiusIncrement = (finalRadius - initialRadius) / (totalAngle / distanceBetweenPoints);
        double angleIncrement = distanceBetweenPoints / initialRadius;

        double angle = 0;
        double radius = initialRadius;

        while (angle <= totalAngle) {
            double x = center.x() + radius * Math.cos(angle + rotationRadians);
            double y = center.y() - radius * Math.sin(angle + rotationRadians); // Flip y-axis
            points.add(new PointMouseInput(x, y));

            angle += angleIncrement;
            radius += radiusIncrement * angleIncrement;
        }

        SegmentMouseInput segmentInput = new SegmentMouseInput(points);
        return new ShapeMouseInput(List.of(segmentInput));
    }

    // Compare two mouse inputs for similarity within an error margin
    public static boolean compare(ShapeMouseInput playerInput, ShapeMouseInput targetShape, double errorMargin) {
        // Implement a proper comparison algorithm here
        // Placeholder implementation: Check if the average distance between corresponding points is within errorMargin

        List<PointMouseInput> playerPoints = playerInput.getFullPath();
        List<PointMouseInput> targetPoints = targetShape.getFullPath();

        if (playerPoints.size() != targetPoints.size()) {
            // Normalize the number of points
            playerPoints = normalizePoints(playerPoints, targetPoints.size());
        }

        double totalDistance = 0;
        for (int i = 0; i < targetPoints.size(); i++) {
            PointMouseInput playerPoint = playerPoints.get(i);
            PointMouseInput targetPoint = targetPoints.get(i);
            double distance = Math.hypot(playerPoint.x() - targetPoint.x(), playerPoint.y() - targetPoint.y());
            totalDistance += distance;
        }

        double averageDistance = totalDistance / targetPoints.size();
        return averageDistance <= errorMargin;
    }

    // Helper method to normalize the number of points
    private static List<PointMouseInput> normalizePoints(List<PointMouseInput> points, int desiredSize) {
        List<PointMouseInput> normalizedPoints = new ArrayList<>();
        double step = (points.size() - 1) / (double) (desiredSize - 1);
        for (int i = 0; i < desiredSize; i++) {
            int index = (int) Math.round(i * step);
            normalizedPoints.add(points.get(Math.min(index, points.size() - 1)));
        }
        return normalizedPoints;
    }
}