package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.util.LinkedList;
import java.util.List;

public record ShapeMouseInput(List<SegmentMouseInput> mouseInputs) {

    public List<MouseMotionInput> getFullPath() {
        List<MouseMotionInput> path = new LinkedList<>();
        for (SegmentMouseInput segment : mouseInputs()) {
            path.addAll(segment.mouseInputs());
        }
        return path;
    }
}
