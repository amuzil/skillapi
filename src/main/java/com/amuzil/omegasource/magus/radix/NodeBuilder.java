package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.forms.Form;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class NodeBuilder {
	private final Type type;
	private final Map<Form, Node> children;
	private Consumer<Branch> onEnter;
	private Consumer<Branch> onLeave;
	private Consumer<Branch> onTerminate;
	private Condition terminateCondition;

	private NodeBuilder(Type type) {
		this.type = type;
		this.children = new HashMap<>();
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

	public NodeBuilder addChild(Form form, Node child) {
		if (type.canHaveChildren) {
			children.put(form, child);
			return this;
		} else {
			throw cannot("have children");
		}
	}

	public NodeBuilder removeChild(Form form) {
		if (type.canHaveChildren) {
			children.remove(form);
			return this;
		} else {
			throw cannot("have children");
		}
	}

	public NodeBuilder onEnter(Consumer<Branch> onEnter) {
		if (type.canBeEntered) {
			this.onEnter = onEnter;
			return this;
		} else {
			throw cannot("be entered");
		}
	}

	public NodeBuilder onLeave(Consumer<Branch> onLeave) {
		if (type.canBeLeft) {
			this.onLeave = onLeave;
			return this;
		} else {
			throw cannot("be left");
		}
	}

	public NodeBuilder onTerminate(Consumer<Branch> onTerminate) {
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
		return new Node(children, onEnter, onLeave, onTerminate, terminateCondition);
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
