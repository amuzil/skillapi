package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.List;

import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;

public class KeyboardMouseInputModule extends InputModule {

    private Form activeForm = null;

    @Override
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute) {
        //generate condition from InputData.
        Runnable onSuccess = () -> {
            //todo pass formToExecute to the form queue.
            Minecraft mc = Minecraft.getInstance();
            if(mc.level != null && formToExecute != activeForm) {
                Data livingDataCapability = CapabilityHandler.getCapability(mc.player, CapabilityHandler.LIVING_DATA);
                LogManager.getLogger().info("FORM ACTIVATED :" + formToExecute.name());
                livingDataCapability.getTree().moveDown(formToExecute);
                activeForm = formToExecute;
            }
            //reset condition?
        };
        Runnable onFailure = () -> {
            activeForm = null;
            //reset conditions?
           // Magus.radixTree.burn();
        };
        Condition formCondition = new ConditionBuilder()
                .fromInputData(formExecutionInputs)
                .build();
        if(formCondition != null) {
            //Register listeners for condition created.
            formCondition.register(onSuccess, onFailure);
            //add condition to InputModule registry so that it can be tracked.
            _formInputs.put(formCondition, formToExecute);
        } else {
            //todo errors/logging
        }

    }
}
