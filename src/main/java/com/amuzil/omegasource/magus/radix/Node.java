package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.network.packets.client_executed.RegisterModifierListenersPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.UnregisterModifierListenersPacket;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
public class Node {
    private final Map<Form, Node> children;
    private final Consumer<RadixTree> onEnter;
    private final Consumer<RadixTree> onLeave;
    private final Consumer<RadixTree> onTerminate;
    private final Condition terminateCondition;
    private final List<Modifier> modifiers;

    /**
     * @param children           If a condition is fulfilled, the active node moves down to the mapped child node
     * @param onEnter            Called when the active node is moved down from the parent node to this node
     * @param onLeave            Called when the active node is moved down from this node to a child node
     * @param onTerminate        Called when the active node is moved up to the root node because either all children's conditions have expired or the terminate condition has been fulfilled
     * @param terminateCondition If this condition is fulfilled, the active node will be terminated. If it expires, nothing special happens. It doesn't have to expire for the branch to terminate
     */
    public Node(
            Map<Form, Node> children,
            Consumer<RadixTree> onEnter,
            Consumer<RadixTree> onLeave,
            Consumer<RadixTree> onTerminate,
            Condition terminateCondition,
            List<Modifier> modifiers
    ) {
        this.children = children;
        this.onEnter = onEnter;
        this.onLeave = onLeave;
        this.onTerminate = onTerminate;
        this.terminateCondition = terminateCondition;
        this.modifiers = modifiers;
    }

    public Map<Form, Node> children() {
        return children;
    }

    public Consumer<RadixTree> onEnter() {
        // todo: wrap the listener registration in a check if this is in a player or AI context.
        return onEnter;
    }

    public Consumer<RadixTree> onLeave() {
        // todo: wrap the listener unregistration in a check if this is in a player or AI context.
        if(modifiers.size() > 0) {
            modifiers.forEach(modifier -> modifier.print());

            unregisterModifierListeners();
        }
        return onLeave;
    }

    public Consumer<RadixTree> onTerminate() {
        return onTerminate;
    }

    public Condition terminateCondition() {
        return terminateCondition;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void registerModifierListeners(Form lastActivatedForm) {
        CompoundTag listenerInstanceData = new CompoundTag();

        //here we can send information to the client to help build the Modifier Listeners appropriately.
        listenerInstanceData.putString("lastFormActivated", lastActivatedForm.name());

        List<String> modifierTypes = new ArrayList<>();
        modifiers.forEach(type -> modifierTypes.add(type.data().getName()));

        //todo send RegisterModifierListeners packet to register
        // new Modifier listener instances based on the form used to execute this node
//        new RegisterModifierListenersPacket(modifierTypes, listenerInstanceData);
    }

    public void unregisterModifierListeners() {
        //todo send packet to unregister modifier listeners
//        new UnregisterModifierListenersPacket();
    }

    public void addModifierData(ModifierData modifierData) {
        Modifier existingModifier = modifiers.stream()
                .filter(modifier -> modifier.data().getName().equals(modifierData.getName())).findFirst().get();

        ModifierData existingData = existingModifier.data();

        existingData.add(modifierData);
    }
}