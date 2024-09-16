package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.util.List;

public record SegmentMouseInput(List<MouseMotionInput> mouseInputs) {

    public MouseMotionInput start() {
        return mouseInputs.get(0);
    }

    public MouseMotionInput end() {
        return mouseInputs().get(mouseInputs().size() - 1);
    }
}
