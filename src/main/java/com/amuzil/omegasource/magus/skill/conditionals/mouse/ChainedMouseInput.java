package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import java.util.LinkedList;
import java.util.List;

public record ChainedMouseInput(List<MultiMouseInput> mouseInputs) {

    public List<MouseInput> getFullPath() {
        List<MouseInput> path = new LinkedList<>();
        for (MultiMouseInput segment : mouseInputs()) {
            path.addAll(segment.mouseInputs());
        }
        return path;
    }
}
