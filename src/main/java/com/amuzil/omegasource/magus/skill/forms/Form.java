package com.amuzil.omegasource.magus.skill.forms;

import com.amuzil.omegasource.magus.registry.Registries;

public class Form {
    private String name;

    public Form(String name) {
        this.name = name;
        Registries.registerForm(this);
    }

    /**
     * Just in case. I don't think we care about the MOD_ID as forge handles that.
     * @param name
     * @param modID
     */
    public Form(String name, String modID) {
        this(name);
    }

    public String name() {
        return name;
    }
}
