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

    public static boolean compare(ShapeMouseInput playerInput, ShapeMouseInput targetShape, double errorMargin) {
        // Resample the player's input to have a fixed number of points
        List<PointMouseInput> resampledPlayerPoints = resamplePoints(playerInput.getFullPath(), 64);

        // Rotate to align with the target shape
        List<PointMouseInput> rotatedPlayerPoints = rotateToZero(resampledPlayerPoints);

        // Scale to a reference square
        List<PointMouseInput> scaledPlayerPoints = scaleToSquare(rotatedPlayerPoints, 250);

        // Translate to origin
        List<PointMouseInput> normalizedPlayerPoints = translateToOrigin(scaledPlayerPoints);

        // Prepare the target shape in the same way
        List<PointMouseInput> resampledTargetPoints = resamplePoints(targetShape.getFullPath(), 64);
        List<PointMouseInput> rotatedTargetPoints = rotateToZero(resampledTargetPoints);
        List<PointMouseInput> scaledTargetPoints = scaleToSquare(rotatedTargetPoints, 250);
        List<PointMouseInput> normalizedTargetPoints = translateToOrigin(scaledTargetPoints);

        // Compute the path distance between the two point paths
        double distance = pathDistance(normalizedPlayerPoints, normalizedTargetPoints);

        return distance <= errorMargin;
    }

    // Resample the points to a fixed number
    private static List<PointMouseInput> resamplePoints(List<PointMouseInput> points, int n) {
        double pathLength = getPathLength(points);
        double interval = pathLength / (n - 1);
        double D = 0.0;
        List<PointMouseInput> newPoints = new ArrayList<>();
        newPoints.add(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            double d = euclideanDistance(points.get(i - 1), points.get(i));
            if ((D + d) >= interval) {
                double t = (interval - D) / d;
                double x = points.get(i - 1).x() + t * (points.get(i).x() - points.get(i - 1).x());
                double y = points.get(i - 1).y() + t * (points.get(i).y() - points.get(i - 1).y());
                PointMouseInput newPoint = new PointMouseInput(x, y);
                newPoints.add(newPoint);
                points.add(i, newPoint);
                D = 0.0;
            } else {
                D += d;
            }
        }
        if (newPoints.size() == n - 1) {
            newPoints.add(points.get(points.size() - 1));
        }
        return newPoints;
    }

    // Rotate the points so that the indicative angle is at zero
    private static List<PointMouseInput> rotateToZero(List<PointMouseInput> points) {
        PointMouseInput centroid = getCentroid(points);
        double theta = Math.atan2(points.get(0).y() - centroid.y(), points.get(0).x() - centroid.x());
        return rotateBy(points, -theta);
    }

    private static List<PointMouseInput> rotateBy(List<PointMouseInput> points, double angle) {
        PointMouseInput centroid = getCentroid(points);
        List<PointMouseInput> newPoints = new ArrayList<>();
        for (PointMouseInput point : points) {
            double x = (point.x() - centroid.x()) * Math.cos(angle) - (point.y() - centroid.y()) * Math.sin(angle) + centroid.x();
            double y = (point.x() - centroid.x()) * Math.sin(angle) + (point.y() - centroid.y()) * Math.cos(angle) + centroid.y();
            newPoints.add(new PointMouseInput(x, y));
        }
        return newPoints;
    }

    // Scale the points to a reference square
    private static List<PointMouseInput> scaleToSquare(List<PointMouseInput> points, double size) {
        double minX = points.stream().mapToDouble(PointMouseInput::x).min().orElse(0);
        double maxX = points.stream().mapToDouble(PointMouseInput::x).max().orElse(0);
        double minY = points.stream().mapToDouble(PointMouseInput::y).min().orElse(0);
        double maxY = points.stream().mapToDouble(PointMouseInput::y).max().orElse(0);

        double width = maxX - minX;
        double height = maxY - minY;

        List<PointMouseInput> newPoints = new ArrayList<>();
        for (PointMouseInput point : points) {
            double x = (point.x() - minX) * (size / width);
            double y = (point.y() - minY) * (size / height);
            newPoints.add(new PointMouseInput(x, y));
        }
        return newPoints;
    }

    // Translate the points so that the centroid is at the origin
    private static List<PointMouseInput> translateToOrigin(List<PointMouseInput> points) {
        PointMouseInput centroid = getCentroid(points);
        List<PointMouseInput> newPoints = new ArrayList<>();
        for (PointMouseInput point : points) {
            double x = point.x() - centroid.x();
            double y = point.y() - centroid.y();
            newPoints.add(new PointMouseInput(x, y));
        }
        return newPoints;
    }

    // Calculate the path distance between two point paths
    private static double pathDistance(List<PointMouseInput> path1, List<PointMouseInput> path2) {
        double distance = 0.0;
        for (int i = 0; i < path1.size(); i++) {
            distance += euclideanDistance(path1.get(i), path2.get(i));
        }
        return distance / path1.size();
    }

    // Helper methods
    private static double euclideanDistance(PointMouseInput p1, PointMouseInput p2) {
        return Math.hypot(p1.x() - p2.x(), p1.y() - p2.y());
    }

    private static double getPathLength(List<PointMouseInput> points) {
        double length = 0.0;
        for (int i = 1; i < points.size(); i++) {
            length += euclideanDistance(points.get(i - 1), points.get(i));
        }
        return length;
    }

    private static PointMouseInput getCentroid(List<PointMouseInput> points) {
        double xSum = points.stream().mapToDouble(PointMouseInput::x).sum();
        double ySum = points.stream().mapToDouble(PointMouseInput::y).sum();
        return new PointMouseInput(xSum / points.size(), ySum / points.size());
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