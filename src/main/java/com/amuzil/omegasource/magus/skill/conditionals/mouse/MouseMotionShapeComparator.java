package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MouseMotionShapeComparator {

    private final double errorMargin;

    public MouseMotionShapeComparator(double errorMargin) {
        this.errorMargin = errorMargin;
    }

    // Added compareToPath method
    public boolean compareToPath(List<Point2D> path, List<Point2D> targetShape) {
        // Check the size to avoid out-of-bounds errors; you may want to add more sophisticated path matching logic.
        int minSize = Math.min(path.size(), targetShape.size());
        for (int i = 0; i < minSize; i++) {
            Point2D pathPoint = path.get(i);
            Point2D shapePoint = targetShape.get(i);

            // Check if the path points are within the acceptable error margin
            if (pathPoint.distance(shapePoint) > errorMargin) {
                return false;
            }
        }
        return true;
    }

    public boolean compareToLine(List<Point2D> path, Point2D startPoint, Point2D endPoint) {
        List<Point2D> line = generateLine(startPoint, endPoint);
        return comparePaths(path, line);
    }

    public boolean compareToSquare(List<Point2D> path, Point2D center, double sideLength, double rotation) {
        List<Point2D> square = generatePolygon(center, sideLength, 4, rotation);
        return comparePaths(path, square);
    }

    public boolean compareToCircle(List<Point2D> path, Point2D center, double radius) {
        List<Point2D> circle = generateCircle(center, radius);
        return comparePaths(path, circle);
    }

    public boolean compareToPolygon(List<Point2D> path, Point2D center, double sideLength, int sides, double rotation) {
        List<Point2D> polygon = generatePolygon(center, sideLength, sides, rotation);
        return comparePaths(path, polygon);
    }

    public boolean compareToStar(List<Point2D> path, Point2D center, double outerRadius, double innerRadius, int points, double rotation) {
        List<Point2D> star = generateStar(center, outerRadius, innerRadius, points, rotation);
        return comparePaths(path, star);
    }

    public boolean compareToDiamond(List<Point2D> path, Point2D center, double width, double height, double rotation) {
        List<Point2D> diamond = generateDiamond(center, width, height, rotation);
        return comparePaths(path, diamond);
    }

    public boolean compareToSpiral(List<Point2D> path, Point2D center, double initialRadius, double turns, double spacing, double rotation) {
        List<Point2D> spiral = generateSpiral(center, initialRadius, turns, spacing, rotation);
        return comparePaths(path, spiral);
    }

    private boolean comparePaths(List<Point2D> path, List<Point2D> shape) {
        if (path.size() != shape.size()) return false;

        for (int i = 0; i < path.size(); i++) {
            Point2D pathPoint = path.get(i);
            Point2D shapePoint = shape.get(i);

            if (pathPoint.distance(shapePoint) > errorMargin) {
                return false;
            }
        }
        return true;
    }

    private List<Point2D> generateLine(Point2D startPoint, Point2D endPoint) {
        List<Point2D> line = new ArrayList<>();
        line.add(startPoint);
        line.add(endPoint);
        return line;
    }

    private List<Point2D> generateCircle(Point2D center, double radius) {
        List<Point2D> circle = new ArrayList<>();
        int numPoints = 360;

        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            circle.add(new Point2D.Double(x, y));
        }
        return circle;
    }

    private double getAngle(int i, double increment, double rotation) {
        return i * increment + Math.toRadians(rotation);
    }

    private List<Point2D> generatePolygon(Point2D center, double sideLength, int sides, double rotation) {
        List<Point2D> polygon = new ArrayList<>();
        double angleIncrement = 2 * Math.PI / sides;

        for (int i = 0; i < sides; i++) {
            double angle = getAngle(i, angleIncrement, rotation);
            double x = center.getX() + sideLength * Math.cos(angle);
            double y = center.getY() + sideLength * Math.sin(angle);
            polygon.add(new Point2D.Double(x, y));
        }
        return polygon;
    }

    private List<Point2D> generateStar(Point2D center, double outerRadius, double innerRadius, int points, double rotation) {
        List<Point2D> star = new ArrayList<>();
        double angleIncrement = Math.PI / points;

        for (int i = 0; i < 2 * points; i++) {
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double angle = getAngle(i, angleIncrement, rotation);
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            star.add(new Point2D.Double(x, y));
        }
        return star;
    }

    private List<Point2D> generateDiamond(Point2D center, double width, double height, double rotation) {
        List<Point2D> diamond = new ArrayList<>();
        double[] angles = {45, 135, 225, 315};

        for (double angle : angles) {
            double rad = Math.toRadians(angle + rotation);
            double x = center.getX() + (width / 2) * Math.cos(rad);
            double y = center.getY() + (height / 2) * Math.sin(rad);
            diamond.add(new Point2D.Double(x, y));
        }
        return diamond;
    }

    private List<Point2D> generateSpiral(Point2D center, double initialRadius, double turns, double spacing, double rotation) {
        List<Point2D> spiral = new ArrayList<>();
        int numPoints = (int) (turns * 360); // Points per full circle turn

        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / 360.0 + Math.toRadians(rotation);
            double radius = initialRadius + spacing * (i / 360.0);
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            spiral.add(new Point2D.Double(x, y));
        }

        return spiral;
    }
}