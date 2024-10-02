package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.ConditionActivatedPacket;
import com.amuzil.omegasource.magus.skill.elements.Discipline;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.MultiModifierData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RadixTree {
    private static final int NO_MISMATCH = -1;
    private final Node root;
    private Node active;
    private Condition lastActivated = null;
    // Fire is a test
    private Discipline activeDiscipline = null; // Disciplines.FIRE;
    private ConditionPath path;
    private Entity owner;
    private Side side = Side.COMMON;

    public RadixTree(Node root, Side side) {
        this(root);
        this.side = side;
    }

    public RadixTree(Node root) {
        this.root = root;
        this.active = root;
    }

    public RadixTree(Side side) {
        this();
        this.side = side;
    }

    public RadixTree() {
        this(new Node(false));
    }

    private int getFirstMismatchCondition(List<Condition> conditions, List<Condition> edgeCondition) {
        int LENGTH = Math.min(conditions.size(), edgeCondition.size());
        for (int i = 1; i < LENGTH; i++) {
            if (!conditions.get(i).equals(edgeCondition.get(i))) {
                return i;
            }
        }
        return NO_MISMATCH;
    }

    private void activateBranchConditions(List<Condition> conditions) {
        for (Condition condition : conditions) {
            condition.register();
        }
    }

    public void activateAllConditions() {
        activateAllConditions(root, new ArrayList<>());
    }

    private void activateAllConditions(Node current, List<Condition> result) {
        if (current.isComplete) for (Condition condition : result)
            condition.register();

        for (RadixBranch branch : current.branches.values())
            activateAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
    }

    public void deactivateAllConditions() {
        deactivateAllConditions(root, new ArrayList<>());
    }

    private void deactivateAllConditions(Node current, List<Condition> result) {
        if (current.isComplete) for (Condition condition : result)
            condition.unregister();

        for (RadixBranch branch : current.branches.values())
            deactivateAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
    }

    public void resetTree() {
        deactivateAllConditions();
        for (Condition condition : root.getImmediateBranches())
            condition.register();
    }

    // Helpful method to debug and to see all the conditions
    public void printAllConditions() {
        printAllConditions(root, new ArrayList<>());
    }

    private void printAllConditions(Node current, List<Condition> result) {
        if (current.isComplete) System.out.println("Condition: " + result);

        for (RadixBranch branch : current.branches.values())
            printAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
    }

    // Helpful method to debug and to see all the branches in tree format
    public void printAllBranches() {
        printAllBranches(root, "");
    }

    private void printAllBranches(Node current, String indent) {
        int lastValue = current.totalConditions() - 1;
        int i = 0;
        for (RadixBranch branch : current.branches.values()) {
            if (i == lastValue) System.out.println(indent.replace("+", "L") + branch.path);
            else System.out.println(indent.replace("+", "|") + branch.path);
            int length1 = indent.length() / 2 == 0 ? 4 : indent.length() / 2;
            int length2 = branch.path.toString().length() / 3;
            String oldIndent = new String(new char[length1]).replace("\0", " ");
            String lineIndent = new String(new char[length2]).replace("\0", "-");
            String newIndent = oldIndent + "+" + lineIndent + "->";
            i++;
            printAllBranches(branch.next, newIndent);
        }
    }

    // Add conditions to RadixTree - O(n)
    public void insert(List<Condition> conditions) {
        Node current = root;
        int currIndex = 0;

        while (currIndex < conditions.size()) {
            Condition transitionCondition = conditions.get(currIndex);
            RadixBranch currentBranch = current.getTransition(transitionCondition);
            // Iterate forward as we move through the conditions and either sprout a node or move down an existing node
            List<Condition> currCondition = conditions.subList(currIndex, conditions.size());

            // There is no associated branch with the first condition of the current path
            // so simply add the rest of the conditions and finish
            if (currentBranch == null) {
                current.branches.put(transitionCondition, new RadixBranch(new ConditionPath(currCondition)));
                break;
            }

            int splitIndex = getFirstMismatchCondition(currCondition, currentBranch.path.conditions); // uses equals
            if (splitIndex == NO_MISMATCH) {
                // The branch and leftover conditions are the same length
                // so finish and update the next node as a complete node
                if (currCondition.size() == currentBranch.path.conditions.size()) {
                    currentBranch.next.isComplete = true;
                    break;
                } else if (currCondition.size() < currentBranch.path.conditions.size()) {
                    // The leftover condition is a prefix to the edge string, so split
                    List<Condition> suffix = currentBranch.path.conditions.subList(currCondition.size() - 1, currCondition.size());
                    currentBranch.path.conditions = currCondition;
                    Node newNext = new Node(true);
                    Node afterNewNext = currentBranch.next;
                    currentBranch.next = newNext;

                    newNext.addCondition(new ConditionPath(suffix), afterNewNext);
                    break;
                } else { // currStr.length() > currentEdge.label.length()
                    // There are leftover conditions after a perfect match
                    splitIndex = currentBranch.path.conditions.size();
                }
            } else {
                // The leftover conditions and branch conditions differed, so split at point
                List<Condition> suffix = currentBranch.path.conditions.subList(splitIndex, currentBranch.path.conditions.size());
                currentBranch.path.conditions = currentBranch.path.conditions.subList(0, splitIndex);
                Node prevNext = currentBranch.next;
                currentBranch.next = new Node(false);
                currentBranch.next.addCondition(new ConditionPath(suffix), prevNext);
            }

            // Traverse the tree
            current = currentBranch.next;
            currIndex += splitIndex;
        }

        // Only register immediate children conditions
        resetTree();
//        activateAllConditions();
    }

    // Returns matched condition path if found and null if not found - O(n)
    public List<Condition> search(List<Condition> conditions) {
        List<Condition> ret = null;
        Node current = root;
        int currIndex = 0;
        while (currIndex < conditions.size()) {
            Condition currentCondition = conditions.get(currIndex);
            RadixBranch branch = current.getTransition(currentCondition); // uses hashcode
//            RadixBranch branch = current.getMatchedPath(currentCondition);
            if (branch == null) return null;

            if (!branch.path.conditions.isEmpty()) { // Move down logic
                branch.path.conditions.get(0).unregister(); // Stop listening to current condition
                for (Condition condition: branch.next.branches.keySet()) {
                    condition.register(); // Start listening to next child conditions
                }
            }

            List<Condition> currSubCondition = conditions.subList(currIndex, conditions.size());
            if (!Condition.startsWith(currSubCondition, branch.path.conditions))
                return null; // uses equals

            currIndex += branch.path.conditions.size();
            current = branch.next;
            if (ret == null) ret = new ArrayList<>();
            ret = Stream.concat(ret.stream(), branch.path.conditions.stream()).toList();
        }
        return ret;
    }

    public void burn() {
        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        active = null;
        activeDiscipline = null;
    }

    // ---------- Cali's RadixTree Impl ----------

    public void start() {
        setActive(root);
        path = new ConditionPath();
    }

    public void setDiscipline(Discipline discipline) {
        this.activeDiscipline = discipline;
    }

    private void setActive(Node node) {
        active = node;

        /**
         * Automatically handles making conditions move down the tree when satisfied.
         * Need to adjust because it's moving down the tree based on its terminating condition, rather than for each child condition.
         * We only want to do this once per active node.
         */
        // Current Node
        if (active != null && active == root) {
            Condition currentCondition = active.terminateCondition();
            if (currentCondition != null) {
                currentCondition.register(currentCondition.name(), () -> {
                    currentCondition.onSuccess.run();
                    currentCondition.unregister();
                    switch (side) {
                        default -> MagusNetwork.sendToServer(new ConditionActivatedPacket(currentCondition));
                    }

                }, currentCondition.onFailure);
                currentCondition.register();
            }

            // Child Nodes
            for (Condition condition : active.getImmediateBranches()) {
                if (condition != null) {
                    Runnable success;
                    success = () -> {
                        if (condition.onSuccess != null) condition.onSuccess.run();
                        MagusNetwork.sendToServer(new ConditionActivatedPacket(condition));
                        RadixUtil.getLogger().debug("Packet sent.");
                        condition.unregister();
                    };

                    Runnable failure;
                    failure = () -> {
                        condition.onFailure.run();
                        condition.unregister();
                    };
                    String name = condition.name();
                    condition.register(name, success, failure);
                    condition.register();
                }
            }


            if (active.getModifiers().size() > 0 && owner instanceof ServerPlayer player)
                active.registerModifierListeners(activeDiscipline, player);

            if (active.onEnter() != null) {
                active.onEnter().accept(this);
            }

            // Should run the original condition and terminate the tree
            if (active.terminateCondition() != null) {
                Runnable onSuccess = () -> {
                    active.terminateCondition().onSuccess.run();
                    terminate();
                };
                active.terminateCondition().register("", onSuccess, () -> {
                });
            }
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

    // TODO:
    // Rather than relying on the input module to send packets, handle everything in the condition runnables?
    public void moveDown(Condition executedCondition) {
        if (activeDiscipline == null) {
            LogManager.getLogger().info("NO ELEMENT SELECTED");
            return;
        }
        if (active == null) {
            LogManager.getLogger().info("No currently active node to traverse from.");
            return;
        }
        //add the last Node to the activation Path and store its ModifierData's
        if (active.getImmediateChildren().get(executedCondition) == null) {
            LogManager.getLogger().info("Condition met not valid for tree traversal.");
            return;
        }
        if (this.lastActivated != null && active != null) {
            //TODO:
            // Need a better way to ensure the conditions are equivalent
            if (this.lastActivated.name().equals(executedCondition.name())) {
                addModifierData(new MultiModifierData());
                return;
            }
            this.lastActivated.unregister();
            path.addStep(this.lastActivated, active.getModifiers());
        }
        this.lastActivated = executedCondition;

        if (active.getModifiers().size() > 0 && owner instanceof ServerPlayer player) {
            active.unregisterModifierListeners(player);
            //todo remove this its just for testing
            active.getModifiers().forEach(modifier -> modifier.print());
        }

        if (active.children().size() == 0) return;

        if (active.onLeave() != null) {
            active.onLeave().accept(this);
        }


        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        //TODO: THis should not jump levels of the tree!!!!!!
        setActive(active.branches.get(executedCondition).next);
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

    public enum Side {
        CLIENT, COMMON, SERVER
    }

    // Menu = radial menu or a HUD. Other activation types are self explanatory.
    public enum ActivationType {
        MULTIKEY, MENU, HOTKEY, VR
    }

    // Essentially which input module to use.
    // Used for VR, multikey, and hotkey activation types.
    public enum InputType {
        KEYBOARD, MOUSE, MOUSE_MOTION, VR
    }
}