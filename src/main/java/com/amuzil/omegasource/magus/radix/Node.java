package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.client_executed.RegisterModifierListenersPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.UnregisterModifierListenersPacket;
import com.amuzil.omegasource.magus.skill.elements.Discipline;
import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 */
public class Node {
    //This needs to be changed to <Condition, Node>
    private final Map<Condition, Node> children;
    // Need to figure this out...
    private final HashMap<Condition, RadixBranch> branches;
    private final Pair<Condition, Node> parent;
    private final Consumer<RadixTree> onEnter;
    private final Consumer<RadixTree> onLeave;
    private final Consumer<RadixTree> onTerminate;
    private final Condition terminateCondition;
    private final List<ModifierData> modifiers;

    /**
     * @param children           If a condition is fulfilled, the active node moves down to the mapped child node
     * @param onEnter            Called when the active node is moved down from the parent node to this node
     * @param onLeave            Called when the active node is moved down from this node to a child node
     * @param onTerminate        Called when the active node is moved up to the root node because either all children's conditions have expired or the terminate condition has been fulfilled
     * @param terminateCondition If this condition is fulfilled, the active node will be terminated. If it expires, nothing special happens. It doesn't have to expire for the branch to terminate
     */
    public Node(
            Pair<Condition, Node> parent,
            Map<Condition, Node> children,
            Consumer<RadixTree> onEnter,
            Consumer<RadixTree> onLeave,
            Consumer<RadixTree> onTerminate,
            Condition terminateCondition,
            List<Modifier> modifiers
    ) {
        this.parent = parent;
        this.children = children;
        this.onEnter = onEnter;
        this.onLeave = onLeave;
        this.onTerminate = onTerminate;
        this.terminateCondition = terminateCondition;
        this.modifiers = Collections.synchronizedList(modifiers.stream().map(Modifier::data).toList());
        branches = new HashMap<>();
    }

    public Map<Condition, Node> children() {
        return children;
    }

    public Consumer<RadixTree> onEnter() {
        return onEnter;
    }

    public Consumer<RadixTree> onLeave() {
        return onLeave;
    }

    public Pair<Condition, Node> parent() {
        return this.parent;
    }

    public Map<Condition, Node> getImmediateChildren() {
        return children().entrySet().stream()
                .filter(entry ->
                    entry.getValue().parent().getSecond().equals(this)
                            && entry.getValue().parent().getSecond()
                            .terminateCondition().equals(this.terminateCondition())) // Ensure the child's parent is the current node
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public Consumer<RadixTree> onTerminate() {
        return onTerminate;
    }

    public Condition terminateCondition() {
        return terminateCondition;
    }

    public synchronized List<ModifierData> getModifiers() {
        return modifiers;
    }

    public void registerModifierListeners(Discipline activeDiscipline, ServerPlayer player) {
        CompoundTag listenerInstanceData = new CompoundTag();

        //here we can send information to the client to help build the Modifier Listeners appropriately.
        listenerInstanceData.putString("activeElement", activeDiscipline.name());

        List<String> modifierTypes = new ArrayList<>();
        List<ModifierData> modifiers = getModifiers();
        synchronized (modifiers) {
            modifiers.stream()
                    .filter(modifierData -> !modifierData.serversideOnly())
                    .forEach(type -> modifierTypes.add(type.getName()));
        }

        MagusNetwork.sendToClient(new RegisterModifierListenersPacket(modifierTypes, listenerInstanceData), player);
    }

    public void unregisterModifierListeners(ServerPlayer player) {
        MagusNetwork.sendToClient(new UnregisterModifierListenersPacket(), player);
    }

    public synchronized void addModifierData(ModifierData modifierData) {
        List<ModifierData> existingModifiers = getModifiers();
        synchronized(existingModifiers) {
            addModifierData(existingModifiers, modifierData);
        }
    }

    public synchronized void addModifierData(List<ModifierData> modifierData) {
        List<ModifierData> existingModifiers = getModifiers();
        synchronized(existingModifiers) {
            modifierData.forEach(data -> {
                addModifierData(existingModifiers, data);
            });
        }
    }

    private static void addModifierData(List<ModifierData> existingModifiers, ModifierData data) {
        //Log the data being added to the Node
        LogManager.getLogger().info("addModifierData: newData: ");
        data.print();

        //Identify the existing ModifierData record for this type.
        int existingModifierIndex = existingModifiers.indexOf(existingModifiers.stream().filter(mod -> mod.getName().equals(data.getName())).findFirst().get());
        ModifierData currentModifier = existingModifiers.get(existingModifierIndex);

        //Log the data already on the Node.
        LogManager.getLogger().info("addModifierData: oldData: ");
        currentModifier.print();

        //Merge the two modifier instances and log the result.
        LogManager.getLogger().info("addModifierData: after adding together: ");
        currentModifier.add(data);
        currentModifier.print();
        existingModifiers.add(existingModifierIndex, currentModifier);

        //Log proving the data has been updated in the node successfully.
        LogManager.getLogger().info("addModifierData: after setting on the node: ");
        existingModifiers.get(existingModifierIndex).print();
    }
}