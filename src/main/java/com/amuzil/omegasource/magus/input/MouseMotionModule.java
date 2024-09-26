package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.ConditionPath;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MousePointInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseVircle;
import com.amuzil.omegasource.magus.skill.forms.Form;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class MouseMotionModule extends InputModule {
    private final Consumer<TickEvent> tickEventConsumer;
    public List<String> mouseGesture;
    public MouseVircle vircle;
    private Form activeForm = new Form();
    private int ticksSinceActivated = 0;
    private int ticksSinceModifiersSent = 0;

    private final int tickActivationThreshold = 15;
    private final int tickTimeoutThreshold = 60;
    private final int modifierTickThreshold = 10;
    private boolean listen;

    public MouseMotionModule() {
        this.mouseGesture = new ArrayList<>();
        this.listen = true;

        this.tickEventConsumer = tickEvent -> {

            ticksSinceModifiersSent++;
            if (ticksSinceModifiersSent > modifierTickThreshold && !modifierQueue.isEmpty()) {
                sendModifierData();
            }

            if (Magus.keyboardInputModule.keyPressed(Minecraft.getInstance().options.keyShift.getKey().getValue())) {
                Minecraft mci = Minecraft.getInstance();
                assert mci.player != null;
                double x = mci.mouseHandler.xpos();
                double y = mci.mouseHandler.ypos();
                Vec3 lookAngle = mci.player.getLookAngle();
                if (vircle == null) {
                    vircle = new MouseVircle(new MousePointInput(x, y, lookAngle));
                } else {
                    vircle.track(new MousePointInput(x, y, lookAngle));
                    if (vircle.hasMotionDirection()) {
                        mouseGesture.add(vircle.popMotionDirection());
                    }
                }
            } else {
                if (!mouseGesture.isEmpty()) {
//                    System.out.println("Mouse Gesture: " + mouseGesture);
                    mouseGesture.clear();
                }
            }

            if(activeForm.name() != null) {
                ticksSinceActivated++;
                if(ticksSinceActivated >= tickActivationThreshold) {
//                    if (lastActivatedForm != null)
//                        LogManager.getLogger().info("LAST FORM ACTIVATED: " + lastActivatedForm.name() + " | FORM ACTIVATED: " + activeForm.name());
//                    else
//                        LogManager.getLogger().info("FORM ACTIVATED: " + activeForm.name());
//                    MagusNetwork.sendToServer(new ConditionActivatedPacket(activeForm));
                    lastActivatedForm = activeForm;
                    activeForm = new Form();
                    ticksSinceActivated = 0;
                }
            }
            else {
                ticksSinceActivated++;
                if (ticksSinceActivated >= tickTimeoutThreshold) {
                    lastActivatedForm = null;
                    ticksSinceActivated = 0;
                }
            }
        };
    }

    private void sendModifierData() {
        LogManager.getLogger().info("SENDING MODIFIER DATA");
        synchronized (modifierQueue) {
            MagusNetwork.sendToServer(new SendModifierDataPacket(modifierQueue.values().stream().toList()));
            ticksSinceModifiersSent = 0;
            modifierQueue.clear();
        }
    }

    @Override
    public void registerInputData(List<InputData> formExecutionInputs, Form formToExecute, List<Condition> formCondition) {
        //generate condition from InputData.
        ConditionPath path = formToExecute.createPath(formCondition);
        formsTree.insert(path.conditions);
//        Runnable onSuccess = () -> {
//            if(mc.level != null) {
//                //this section is to prevent re-activating
//                // single condition forms when you hold the activation key for Held modifiers
//                if(formToExecute != lastActivatedForm) {
//                    //LogManager.getLogger().info("FORM ACTIVATED: " + formToExecute.name());
//                    activeForm = formToExecute;
//                }
//                ticksSinceActivated = 0;
//            }
//        };
//        Runnable onFailure = () -> {
//            activeForm = new Form();
//            //reset conditions?
//            // Magus.radixTree.burn();
//        };
//
//        if(conditions != null) {
//            //Register listeners for condition created.
//            conditions.register(formToExecute.name(), onSuccess, onFailure);
//            //add condition to InputModule registry so that it can be tracked.
//            formInputs.put(conditions, formToExecute);
//        } else {
//            //todo errors/logging
//        }
    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.class, tickEventConsumer);
    }

    @Override
    public void unRegisterInputs() {
        MinecraftForge.EVENT_BUS.unregister(tickEventConsumer);
        formInputs.forEach((condition, form) -> condition.unregister());
    }

    @Override
    public void registerInputs() {
        formInputs.forEach((condition, form) -> {
            condition.register();
        });
    }

    @Override
    public void toggleListeners() {
        if (!listen) {
            registerListeners();
            registerInputs();
            listen = true;
        } else {
            unRegisterInputs();
            listen = false;
        }
    }

    @Override
    public void resetKeys() {

    }

    @Override
    public boolean keyPressed(int key) {
        return false;
    }
}
