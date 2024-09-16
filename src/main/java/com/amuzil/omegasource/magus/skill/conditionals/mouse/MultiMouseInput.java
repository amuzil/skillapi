package com.amuzil.omegasource.magus.skill.conditionals.mouse;

import com.amuzil.omegasource.magus.input.MouseInputModule;

import java.util.List;

public record MultiMouseInput(List<MouseInput> mouseInputs) {

    public MouseInput start() {
        return mouseInputs.get(0);
    }

    public MouseInput end() {
        return mouseInputs().get(mouseInputs().size() - 1);
    }
}
