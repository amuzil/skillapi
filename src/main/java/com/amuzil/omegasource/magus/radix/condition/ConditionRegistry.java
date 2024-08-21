package com.amuzil.omegasource.magus.radix.condition;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.forms.Form;
import net.minecraft.resources.ResourceLocation;

public class ConditionRegistry {


    public static Condition getConditionByName(String formName) {
        return Registries.CONDITIONS.get().getValue(new ResourceLocation(Magus.MOD_ID, formName));
    }

    public static Condition getConditionByName(String modID, String formName) {
        return Registries.CONDITIONS.get().getValue(new ResourceLocation(modID, formName));
    }
}
