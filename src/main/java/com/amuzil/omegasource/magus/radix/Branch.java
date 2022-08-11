package com.amuzil.omegasource.magus.radix;

import java.util.HashMap;
import java.util.LinkedList;

public class Branch {
	private final LinkedList<Step> path;

	private final HashMap<Class<? extends Leaf<?>>, Leaf<?>> leaves;

	public Branch() {
		path = new LinkedList<>();
		leaves = new HashMap<>();
	}

	public void burn() {
		path.clear();
		leaves.values().forEach(Leaf::burn);
		leaves.clear();
	}

	public void reset(Node root) {
		path.clear();
		addStep(null, root);
		leaves.values().forEach(Leaf::reset);
	}

	public void addStep(Condition activator, Node node) {
		path.add(new Step(activator, node));
	}

	public <T> boolean registerLeaf(Class<Leaf<T>> type, Leaf<T> leaf) {
		if (leaves.containsKey(type)) {
			return false;
		} else {
			leaves.put(type, leaf);
			return true;
		}
	}

	public <T> void resetLeaf(Class<Leaf<T>> type) {
		leaves.get(type).reset();
	}

    public LinkedList<Step> getPath() {
        // Return a copy of the path.
        return new LinkedList<>(path);
    }

	@SuppressWarnings("unchecked")
	public <T> T measureLeaf(Class<Leaf<T>> type) {
		return (T) leaves.get(type).measure();
	}

	public record Step(Condition activator, Node node) {

	}
}

