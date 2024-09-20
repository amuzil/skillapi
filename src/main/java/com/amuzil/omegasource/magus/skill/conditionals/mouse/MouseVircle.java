package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MouseVircle {
    // Class that represents a virtual circle around the cursor

    private float sphereRadius = 0.7F;
    private Vec3 centroid, aimDirection;
    private String motionDirection = "";
    public List<MousePointInput> mouseInputs = new ArrayList<>();

    public MouseVircle(MousePointInput point) {
        this.centroid = point.lookAngle();
        this.mouseInputs.add(point);
    }
    
    public void track(MousePointInput point) {
        mouseInputs.add(point);
        generateVircle(point.lookAngle());
    }

    public void generateVircle(Vec3 point) {
        if (!hasPoint(point)) {
            centroid = point;
            MouseMotionInput motionInputs = new MouseMotionInput(mouseInputs);
            motionDirection = motionInputs.getMotionDirection();
            aimDirection = motionInputs.getAimDirection();
            mouseInputs.clear();
//            System.out.println("Moved -> " + motionDirection);
        }
    }

    public boolean hasPoint(Vec3 point) {
        // Let the sphere's centre coordinates be (cx, cy, cz) and its radius be r,
        // then point (x, y, z) is in the sphere if sqrt( (x−cx)^2 +  (y−cy)^2 + (z−cz)^2 ) <= r.
        double xcx = Math.pow(point.x - centroid.x, 2);
        double ycy = Math.pow(point.y - centroid.y, 2);
        double zcz = Math.pow(point.z - centroid.z, 2);
        double radial_dist = Math.sqrt(xcx + ycy + zcz);
        return radial_dist <= sphereRadius;
    }

    public boolean hasMotionDirection() {
        return !motionDirection.isEmpty();
    }

    public String popMotionDirection() {
        String popValue = motionDirection;
        motionDirection = "";
        return popValue;
    }

    public Vec3 getAimDirection() {
        return aimDirection;
    }
}
