package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.util.LinkedList;
import java.util.List;

public record MouseShapeInput(List<MouseMotionInput> mouseInputs) {

    public List<MousePointInput> getFullPath() {
        List<MousePointInput> path = new LinkedList<>();
        for (MouseMotionInput segment : mouseInputs()) {
            path.addAll(segment.mouseInputs());
        }
        return path;
    }
}
