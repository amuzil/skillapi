package com.amuzil.omegasource.magus.radix.condition.input;

import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.skill.forms.Form;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public class FormCondition extends Condition {
    private final InputModule module;
    private final int timeout;
    private final Form form;
    private final Consumer<TickEvent.ClientTickEvent> tickEvent;
    int ticksWaiting;

    public FormCondition(Form form, int timeout, InputModule module) {
        this.setName(form.name());
        this.form = form;
        this.timeout = timeout;
        this.module = module;
        this.ticksWaiting = 0;

        tickEvent = clientTickEvent -> {
            if (module.getLastActivatedForm().name().equals(form.name())) {
                this.onSuccess.run();
                reset();
            }
            else {
                if (timeout > -1) {
                    ticksWaiting++;
                    if (ticksWaiting > timeout) {
                        this.onFailure.run();
                        reset();
                    }
                }
            }
        };
    }

    public Form getForm() {
        return this.form;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public InputModule getModule() {
        return this.module;
    }
    public void reset() {
        this.ticksWaiting = 0;
    }

    @Override
    public void register() {
        super.register();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class,
                tickEvent);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(tickEvent);
    }

    @Override
    public String name() {
        return "form_activate";
    }
}
