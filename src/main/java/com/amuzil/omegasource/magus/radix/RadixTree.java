package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.server_executed.ConditionActivatedPacket;
import com.amuzil.omegasource.magus.skill.elements.Discipline;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.data.MultiModifierData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.stream.Stream;

public class RadixTree {
    private Node root;
    private static final int NO_MISMATCH = -1;
    private Node active;
    private Condition lastActivated = null;
    // Fire is a test
    private Discipline activeDiscipline = null; //Disciplines.FIRE;
    private ConditionPath path;
    private Entity owner;

    public RadixTree(Node root) {
        this.root = root;
        this.active = root;
    }

    public RadixTree() {
        root = new Node(false);
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

    // Helpful method to debug and to see all the gestures
    public void printAllConditions() {
        printAllConditions(root, new ArrayList<>());
    }

    private void printAllConditions(Node current, List<Condition> result) {
        if (current.isComplete)
            System.out.println("Condition: " + result);

        for (RadixBranch branch: current.branches.values()) {
            printAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
        }
    }

    // Helpful method to debug and to see all the gestures' paths in tree format
    public void printAllBranches() {
        printAllBranches(root, "");
    }

    private void printAllBranches(Node current, String indent) {
        int lastValue = current.totalConditions()-1; int i = 0;
        for (RadixBranch branch: current.branches.values()) {
            if (i == lastValue)
                System.out.println(indent.replace("+", "L") + branch.path);
            else
                System.out.println(indent.replace("+", "|") + branch.path);
            int length1 = indent.length() / 2 == 0 ? 4 : indent.length() / 2;
            int length2 = branch.path.toString().length() / 3;
            String oldIndent = new String(new char[length1]).replace("\0", " ");
            String lineIndent = new String(new char[length2]).replace("\0", "-");
            String newIndent = oldIndent + "+" + lineIndent + "->"; i++;
            printAllBranches(branch.next, newIndent);
        }
    }

    public void insert(List<Condition> conditions) {
        Node current = root;
        int currIndex = 0;

        //Iterative approach
        while (currIndex < conditions.size()) {
            Condition transitionCondition = conditions.get(currIndex);
            RadixBranch currentPath = current.getTransition(transitionCondition);
            //Updated version of the input gesture
            List<Condition> currGesture = conditions.subList(currIndex, conditions.size());

            //There is no associated edge with the first character of the current string
            //so simply add the rest of the string and finish
            if (currentPath == null) {
                current.branches.put(transitionCondition, new RadixBranch(new ConditionPath(currGesture)));
                break;
            }

            int splitIndex = getFirstMismatchCondition(currGesture, currentPath.path.conditions);
            if (splitIndex == NO_MISMATCH) {
                //The edge and leftover string are the same length
                //so finish and update the next node as a gesture node
                if (currGesture.size() == currentPath.path.conditions.size()) {
                    currentPath.next.isComplete = true;
                    break;
                } else if (currGesture.size() < currentPath.path.conditions.size()) {
                    //The leftover gesture is a prefix to the edge string, so split
                    List<Condition> suffix = currentPath.path.conditions.subList(currGesture.size()-1, currGesture.size());
                    currentPath.path.conditions = currGesture;
                    Node newNext = new Node(true);
                    Node afterNewNext = currentPath.next;
                    currentPath.next = newNext;

                    newNext.addCondition(new ConditionPath(suffix), afterNewNext);
                    break;
                } else { //currStr.length() > currentEdge.label.length()
                    //There is leftover string after a perfect match
                    splitIndex = currentPath.path.conditions.size();
                }
            } else {
                //The leftover string and edge string differed, so split at point
                List<Condition> suffix = currentPath.path.conditions.subList(splitIndex, currentPath.path.conditions.size());
                currentPath.path.conditions = currentPath.path.conditions.subList(0, splitIndex);
                Node prevNext = currentPath.next;
                currentPath.next = new Node(false);
                currentPath.next.addCondition(new ConditionPath(suffix), prevNext);
            }

            //Traverse the tree
            current = currentPath.next;
            currIndex += splitIndex;
        }
    }

    // ---------- Cali's RadixTree Impl ----------

    public void burn() {
        if (active.terminateCondition() != null) {
            active.terminateCondition().unregister();
        }

        active = null;
        activeDiscipline = null;
    }

    public void start() {
        setActive(root);
        path = new ConditionPath();
    }

    private void setActive(Discipline discipline) {
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
                currentCondition.register(() -> {
                    currentCondition.onSuccess.run();
                    MagusNetwork.sendToServer(new ConditionActivatedPacket(currentCondition));
                }, currentCondition.onFailure);
            }

            // Child Nodes
            for (Map.Entry<Condition, Node> child : active.getImmediateChildren().entrySet()) {
                //TODO: Find way to prevent overwriting but also prevent doubly sending packets.
                Condition condition = child.getKey();
                if (condition != null) {
                    Runnable success;
                    success = () -> {
                        if (condition.onSuccess != null)
                            condition.onSuccess.run();
                        MagusNetwork.sendToServer(new ConditionActivatedPacket(condition));
                        RadixUtil.getLogger().debug("Packet sent.");
                        condition.unregister();
                    };
                    condition.register(success, condition.onFailure);
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
                active.terminateCondition().register(onSuccess, () -> {
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