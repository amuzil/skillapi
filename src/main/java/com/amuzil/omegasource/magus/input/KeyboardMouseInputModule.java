package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.List;
import java.util.function.Consumer;

import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;

public class KeyboardMouseInputModule extends InputModule {

    private final Consumer<TickEvent> tickEventConsumer;

    private Form lastActivatedForm = null;
    private Form activeForm = null;
    private int ticksSinceActivated = 0;

    //todo make this threshold configurable
    private final int tickActivationThreshold = 4;
    Minecraft mc = Minecraft.getInstance();

    public KeyboardMouseInputModule() {
        tickEventConsumer = tickEvent -> {
            if(activeForm != null) {
                ticksSinceActivated++;
            }

            if(ticksSinceActivated >= tickActivationThreshold) {
                Data livingDataCapability = CapabilityHandler.getCapability(mc.player, CapabilityHandler.LIVING_DATA);
                LogManager.getLogger().info("FORM ACTIVATED :" + activeForm.name());
                livingDataCapability.getTree().moveDown(activeForm);
                lastActivatedForm = activeForm;
                activeForm = null;
                ticksSinceActivated = 0;
            }
        };
        MinecraftForge.EVENT_BUS.addListener(tickEventConsumer);
    }

    @Override
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute) {
        //generate condition from InputData.
        Runnable onSuccess = () -> {
            if(mc.level != null) {
                //this section is to prevent re-activating
                // single condition forms when you hold the activation key for Held modifiers
                if(formToExecute != lastActivatedForm) {
                    activeForm = formToExecute;
                }
                ticksSinceActivated = 0;
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

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        _formInputs.forEach((condition, form) -> condition.unregister());
    }
}
