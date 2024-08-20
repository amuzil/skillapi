package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.elements.Discipline;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.MultiModifierData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class RadixTree {
    private final Node root;
    private Node active;
    private Condition lastActivated = null;
    // Fire is a test
    private Discipline activeDiscipline = null;//Disciplines.FIRE;
    private RadixPath path;
    private Entity owner;

    public RadixTree(Node root) {
        this.root = root;
        this.active = root;
    }

    public void burn() {
        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        active = null;
        activeDiscipline = null;
    }

    public void start() {
        setActive(root);
        path = new RadixPath();
    }

    private void setActive(Discipline discipline) {
        this.activeDiscipline = discipline;
    }

    private void setActive(Node node) {
        active = node;

        if(active.getModifiers().size() > 0 && owner instanceof ServerPlayer player)
            active.registerModifierListeners(activeDiscipline, player);

        if (active.onEnter() != null) {
            active.onEnter().accept(this);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().register(this::terminate, () -> {
            });
        }
    }

    // Called when either the node's terminate condition is fulfilled or all active child conditions have expired
    private void terminate() {

        System.out.println("Nice!");

        if (active.onTerminate() != null) {
            active.onTerminate().accept(this);
        }

        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        start();
    }

    public void moveDown(Condition executedCondition) {
        if(activeDiscipline == null) {
            LogManager.getLogger().info("NO ELEMENT SELECTED");
            return;
        }
        //add the last Node to the activation Path and store its ModifierData's

        if (this.lastActivated != null && active != null) {
            //TODO: 
            // Need a better way to ensure the conditions are equivalent
            if(this.lastActivated.name().equals(executedCondition.name())) {
                addModifierData(new MultiModifierData());
                return;
            }
            path.addStep(this.lastActivated, active.getModifiers());
        }
        this.lastActivated = executedCondition;

        if(active.getModifiers().size() > 0 && owner instanceof ServerPlayer player) {
            active.unregisterModifierListeners(player);
            //todo remove this its just for testing
            active.getModifiers().forEach(modifier -> modifier.print());
        }

        if(active.children().size() == 0) return;

        if (active.onLeave() != null) {
            active.onLeave().accept(this);
        }


        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        setActive(active.children().get(executedCondition));
    }

    public void expire() {
        terminate();
    }

    public void addModifierData(List<ModifierData> modifierData) {
        active.addModifierData(modifierData);
    }

    public void addModifierData(ModifierData modifierData) {
        active.addModifierData(modifierData);
    }

    public void setOwner(Entity entity) {
        this.owner = entity;
    }
}