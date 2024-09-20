package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import net.minecraft.client.Minecraft;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MousePathComparator {

    public MousePathComparator(double errorMargin) {
    }

    // Generate a circular shape centered at the player's mouse position with rotation and scaling
    public static MouseShapeInput generateCircle(MousePointInput center, double scale, double rotationDegrees, double distanceBetweenPoints) {
        List<MousePointInput> points = new ArrayList<>();

        // Calculate the number of points based on circumference and desired distance between points
        double circumference = 2 * Math.PI * scale;
        int numPoints = Math.max(10, (int) (circumference / distanceBetweenPoints));

        // Convert rotation to radians
        double rotationRadians = Math.toRadians(rotationDegrees);

        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double x = center.x() + scale * Math.cos(angle + rotationRadians);
            double y = center.y() - scale * Math.sin(angle + rotationRadians); // Flip y-axis
            if(Minecraft.getInstance().player != null)
                points.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        MouseMotionInput segmentInput = new MouseMotionInput(points);
        return new MouseShapeInput(List.of(segmentInput));
    }

    // Generate a straight line starting from the player's mouse position with rotation and scaling
    public static MouseShapeInput generateLine(MousePointInput start, double scale, double rotationDegrees, double distanceBetweenPoints) {
        List<MousePointInput> points = new ArrayList<>();

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
            if (Minecraft.getInstance().player != null)
                points.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        MouseMotionInput line = new MouseMotionInput(points);
        return new MouseShapeInput(List.of(line));
    }

    // Generate a polygon centered at the player's mouse position with rotation and scaling
    public static MouseShapeInput generatePolygon(MousePointInput center, int sides, double scale, double rotationDegrees, double distanceBetweenPoints, boolean isDiamond) {
        List<MousePointInput> vertices = new ArrayList<>();

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
            if (Minecraft.getInstance().player != null)
                vertices.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }

        // Interpolate points between vertices based on distanceBetweenPoints
        return MouseDataBuilder.createPolygonWithInterpolation(vertices, distanceBetweenPoints);
    }

    // Generate a star shape centered at the player's mouse position with rotation and scaling
    public static MouseShapeInput generateStar(MousePointInput center, int pointsCount, double scale, double rotationDegrees, double distanceBetweenPoints) {
        List<MousePointInput> vertices = new ArrayList<>();
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
            if (Minecraft.getInstance().player != null)
                vertices.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }

        // Interpolate points between vertices based on distanceBetweenPoints
        return MouseDataBuilder.createPolygonWithInterpolation(vertices, distanceBetweenPoints);
    }

    // Spiral!!!
    public static MouseShapeInput generateSpiral(MousePointInput center, int turns, double initialRadius, double finalRadius, double rotationDegrees, double distanceBetweenPoints) {
        List<MousePointInput> points = new ArrayList<>();

        double rotationRadians = Math.toRadians(rotationDegrees);
        double totalAngle = 2 * Math.PI * turns;
        double radiusIncrement = (finalRadius - initialRadius) / (totalAngle / distanceBetweenPoints);
        double angleIncrement = distanceBetweenPoints / initialRadius;

        double angle = 0;
        double radius = initialRadius;

        while (angle <= totalAngle) {
            double x = center.x() + radius * Math.cos(angle + rotationRadians);
            double y = center.y() - radius * Math.sin(angle + rotationRadians); // Flip y-axis
            if (Minecraft.getInstance().player != null)
                points.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));

            angle += angleIncrement;
            radius += radiusIncrement * angleIncrement;
        }

        MouseMotionInput segmentInput = new MouseMotionInput(points);
        return new MouseShapeInput(List.of(segmentInput));
    }

    public static boolean compare(MouseShapeInput playerInput, MouseShapeInput targetShape, double errorMargin) {
        // Resample the player's input to have a fixed number of points
        List<MousePointInput> resampledPlayerPoints = resamplePoints(playerInput.getFullPath(), 64);

        // Rotate to align with the target shape
        List<MousePointInput> rotatedPlayerPoints = rotateToZero(resampledPlayerPoints);

        // Scale to a reference square
        List<MousePointInput> scaledPlayerPoints = scaleToSquare(rotatedPlayerPoints, 250);

        // Translate to origin
        List<MousePointInput> normalizedPlayerPoints = translateToOrigin(scaledPlayerPoints);

        // Prepare the target shape in the same way
        List<MousePointInput> resampledTargetPoints = resamplePoints(targetShape.getFullPath(), 64);
        List<MousePointInput> rotatedTargetPoints = rotateToZero(resampledTargetPoints);
        List<MousePointInput> scaledTargetPoints = scaleToSquare(rotatedTargetPoints, 250);
        List<MousePointInput> normalizedTargetPoints = translateToOrigin(scaledTargetPoints);

        // Compute the path distance between the two point paths
        double distance = pathDistance(normalizedPlayerPoints, normalizedTargetPoints);

        return distance <= errorMargin;
    }

    // Resample the points to a fixed number
    private static List<MousePointInput> resamplePoints(List<MousePointInput> points, int n) {
        double pathLength = getPathLength(points);
        double interval = pathLength / (n - 1);
        double D = 0.0;
        List<MousePointInput> newPoints = new ArrayList<>();
        newPoints.add(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            double d = euclideanDistance(points.get(i - 1), points.get(i));
            if ((D + d) >= interval) {
                double t = (interval - D) / d;
                double x = points.get(i - 1).x() + t * (points.get(i).x() - points.get(i - 1).x());
                double y = points.get(i - 1).y() + t * (points.get(i).y() - points.get(i - 1).y());
                MousePointInput newPoint = null;
                if (Minecraft.getInstance().player != null)
                    newPoint = new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle());
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
    private static List<MousePointInput> rotateToZero(List<MousePointInput> points) {
        MousePointInput centroid = getCentroid(points);
        double theta = Math.atan2(points.get(0).y() - centroid.y(), points.get(0).x() - centroid.x());
        return rotateBy(points, -theta);
    }

    private static List<MousePointInput> rotateBy(List<MousePointInput> points, double angle) {
        MousePointInput centroid = getCentroid(points);
        List<MousePointInput> newPoints = new ArrayList<>();
        for (MousePointInput point : points) {
            double x = (point.x() - centroid.x()) * Math.cos(angle) - (point.y() - centroid.y()) * Math.sin(angle) + centroid.x();
            double y = (point.x() - centroid.x()) * Math.sin(angle) + (point.y() - centroid.y()) * Math.cos(angle) + centroid.y();
            if (Minecraft.getInstance().player != null)
                newPoints.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        return newPoints;
    }

    // Scale the points to a reference square
    private static List<MousePointInput> scaleToSquare(List<MousePointInput> points, double size) {
        double minX = points.stream().mapToDouble(MousePointInput::x).min().orElse(0);
        double maxX = points.stream().mapToDouble(MousePointInput::x).max().orElse(0);
        double minY = points.stream().mapToDouble(MousePointInput::y).min().orElse(0);
        double maxY = points.stream().mapToDouble(MousePointInput::y).max().orElse(0);

        double width = maxX - minX;
        double height = maxY - minY;

        List<MousePointInput> newPoints = new ArrayList<>();
        for (MousePointInput point : points) {
            double x = (point.x() - minX) * (size / width);
            double y = (point.y() - minY) * (size / height);
            if (Minecraft.getInstance().player != null)
                newPoints.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        return newPoints;
    }

    // Translate the points so that the centroid is at the origin
    private static List<MousePointInput> translateToOrigin(List<MousePointInput> points) {
        MousePointInput centroid = getCentroid(points);
        List<MousePointInput> newPoints = new ArrayList<>();
        for (MousePointInput point : points) {
            double x = point.x() - centroid.x();
            double y = point.y() - centroid.y();
            if (Minecraft.getInstance().player != null)
                newPoints.add(new MousePointInput(x, y, Minecraft.getInstance().player.getLookAngle()));
        }
        return newPoints;
    }

    // Calculate the path distance between two point paths
    private static double pathDistance(List<MousePointInput> path1, List<MousePointInput> path2) {
        double distance = 0.0;
        for (int i = 0; i < path1.size(); i++) {
            distance += euclideanDistance(path1.get(i), path2.get(i));
        }
        return distance / path1.size();
    }

    // Helper methods
    private static double euclideanDistance(MousePointInput p1, MousePointInput p2) {
        return Math.hypot(p1.x() - p2.x(), p1.y() - p2.y());
    }

    private static double getPathLength(List<MousePointInput> points) {
        double length = 0.0;
        for (int i = 1; i < points.size(); i++) {
            length += euclideanDistance(points.get(i - 1), points.get(i));
        }
        return length;
    }

    private static MousePointInput getCentroid(List<MousePointInput> points) {
        double xSum = points.stream().mapToDouble(MousePointInput::x).sum();
        double ySum = points.stream().mapToDouble(MousePointInput::y).sum();
        assert Minecraft.getInstance().player != null;
        return new MousePointInput(xSum / points.size(), ySum / points.size(), Minecraft.getInstance().player.getLookAngle());
    }

    // Helper method to normalize the number of points
    private static List<MousePointInput> normalizePoints(List<MousePointInput> points, int desiredSize) {
        List<MousePointInput> normalizedPoints = new ArrayList<>();
        double step = (points.size() - 1) / (double) (desiredSize - 1);
        for (int i = 0; i < desiredSize; i++) {
            int index = (int) Math.round(i * step);
            normalizedPoints.add(points.get(Math.min(index, points.size() - 1)));
        }
        return normalizedPoints;
    }

    public boolean compareToPath(List<Point2D> mousePath, List<Point2D> point2DS) {
        return true;
    }
}