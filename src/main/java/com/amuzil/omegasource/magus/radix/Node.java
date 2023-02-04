package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @param children           If a condition is fulfilled, the active node moves down to the mapped child node
 * @param onEnter            Called when the active node is moved down from the parent node to this node
 * @param onLeave            Called when the active node is moved down from this node to a child node
 * @param onTerminate        Called when the active node is moved up to the root node because either all children's conditions have expired or the terminate condition has been fulfilled
 * @param terminateCondition If this condition is fulfilled, the active node will be terminated. If it expires, nothing special happens. It doesn't have to expire for the branch to terminate
 */
public record Node(
        Map<Form, Node> children,
        Consumer<RadixTree> onEnter,
        Consumer<RadixTree> onLeave,
        Consumer<RadixTree> onTerminate,
        Condition terminateCondition
) {

}