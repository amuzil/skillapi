package com.amuzil.omegasource.magus.skill.util.traits;

import com.amuzil.omegasource.magus.registry.Registries;

public class DataTraits  {

    public static void register() {
        Registries.DATA_TRAITS.register("livingData", new LivingDataTrait());
    }
}
