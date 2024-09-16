package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.util.List;

public record SegmentMouseInput(List<PointMouseInput> mouseInputs) {

    public PointMouseInput start() {
        return mouseInputs.get(0);
    }

    public PointMouseInput end() {
        return mouseInputs().get(mouseInputs().size() - 1);
    }
}
