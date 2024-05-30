package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.Modifier;
import com.mojang.datafixers.util.Pair;

import java.util.*;
import java.util.function.Consumer;

public class NodeBuilder {
	private final Type type;
	private final Map<Condition, Node> children;
	private Consumer<RadixTree> onEnter;
	private Consumer<RadixTree> onLeave;
	private Consumer<RadixTree> onTerminate;
	private Condition terminateCondition;
	private final List<Modifier> availableModifiers;

	private NodeBuilder(Type type) {
		this.type = type;
		this.children = new HashMap<>();
		this.availableModifiers = new ArrayList<>();
		this.onEnter = null;
		this.onLeave = null;
		this.onTerminate = null;
		this.terminateCondition = null;
	}

	public static NodeBuilder root() {
		return new NodeBuilder(Type.ROOT);
	}

	public static NodeBuilder middle() {
		return new NodeBuilder(Type.MIDDLE);
	}

	public static NodeBuilder end() {
		return new NodeBuilder(Type.END);
	}

	private IllegalStateException cannot(String message) {
		return new IllegalStateException("A " + type.name().toLowerCase(Locale.ROOT) + " node cannot " + message);
	}

	public NodeBuilder addChild(Condition condition, Node child) {
		if (type.canHaveChildren) {
			children.put(condition, child);
			return this;
		} else {
			throw cannot("Have Children");
		}
	}

	public NodeBuilder addChildren(Pair<Condition, Node>... children) {
		if (type.canHaveChildren) {
			for (Pair<Condition, Node> child : children)
				addChild(child.getFirst(), child.getSecond());
			return this;
		}
		else throw cannot("Have Children");
	}

	public NodeBuilder addModifiers(List<Modifier> modifiers) {
		this.availableModifiers.addAll(modifiers);
		return this;
	}

	public NodeBuilder addModifiers(Modifier... modifiers) {
		Collections.addAll(this.availableModifiers, modifiers);
		return this;
	}

	public NodeBuilder addModifier(Modifier modifier) {
		this.availableModifiers.add(modifier);

		return this;
	}

	public NodeBuilder removeChild(Form form) {
		if (type.canHaveChildren) {
			children.remove(form);
			return this;
		} else {
			throw cannot("have children");
		}
	}

	public NodeBuilder onEnter(Consumer<RadixTree> onEnter) {
		if (type.canBeEntered) {
			this.onEnter = onEnter;
			return this;
		} else {
			throw cannot("be entered");
		}
	}

	public NodeBuilder onLeave(Consumer<RadixTree> onLeave) {
		if (type.canBeLeft) {
			this.onLeave = onLeave;
			return this;
		} else {
			throw cannot("be left");
		}
	}

	public NodeBuilder onTerminate(Consumer<RadixTree> onTerminate) {
		if (type.canBeTerminated) {
			this.onTerminate = onTerminate;
			return this;
		} else {
			throw cannot("be terminated");
		}
	}

	public NodeBuilder terminateWhen(Condition terminateCondition) {
		if (type.canBeTerminated) {
			this.terminateCondition = terminateCondition;
			return this;
		} else {
			throw cannot("be terminated");
		}
	}

	public Node build() {
		return new Node(children, onEnter, onLeave, onTerminate, terminateCondition, availableModifiers);
	}

	private enum Type {
		ROOT(true, false, true, false),
		MIDDLE(true, true, true, true),
		END(false, true, false, false);

		final boolean canHaveChildren;
		final boolean canBeEntered;
		final boolean canBeLeft;
		final boolean canBeTerminated;

		Type(boolean canHaveChildren, boolean canBeEntered, boolean canBeLeft, boolean canBeTerminated) {
			this.canHaveChildren = canHaveChildren;
			this.canBeEntered = canBeEntered;
			this.canBeLeft = canBeLeft;
			this.canBeTerminated = canBeTerminated;
		}
	}
}
