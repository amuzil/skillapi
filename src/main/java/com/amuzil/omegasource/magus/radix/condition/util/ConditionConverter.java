package com.amuzil.omegasource.magus.radix.condition.util;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.Condition.Result;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressCondition;
import com.amuzil.omegasource.magus.skill.activateable.key.KeyCombination;
import com.amuzil.omegasource.magus.skill.activateable.key.KeyInput;
import com.amuzil.omegasource.magus.skill.activateable.key.KeyPermutation;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.event.TickEvent;

import java.util.LinkedList;

/**
 * Takes data and converts it into a corresponding data structure of conditions.
 * Still has to be turned into tree.
 */
public class ConditionConverter {
	/**
	 * Minimum amount of ticks a key must be pressed for it to be considered a held condition.
	 */
	public static final int HELD_THRESHOLD = 3;
	public static final int TIMEOUT_THRESHOLD = 50;

	public static LinkedList<Condition> combinationToConditions(KeyCombination combination) {
		return combination.keys().stream().map(ConditionConverter::permutationToConditions)
			.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
	}

	public static LinkedList<Condition> permutationToConditions(KeyPermutation permutation) {
		// TODO ensure the order is preserved
		return permutation.keys().stream().map(ConditionConverter::keyToConditions)
			.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
	}

	public static LinkedList<Condition> keyToConditions(KeyInput key) {
		LinkedList<Condition> conditions = new LinkedList<>();

		// Any time less than this is just a key press.
		// TODO: Adjust timeout to be per node.
		conditions.add(key.held() > HELD_THRESHOLD
			? new KeyHoldCondition(key.key(), key.held(), TIMEOUT_THRESHOLD)
			: new KeyPressCondition(key.key(), TIMEOUT_THRESHOLD)
		);

		if (key.minDelay() > 0) {
			//TODO: Fix this to account for "action keys".
			conditions.add(new TickTimedCondition(
				TickEvent.Type.CLIENT, TickEvent.Phase.START,
				key.maxDelay(), Result.SUCCESS,
				new EventCondition<Key>(event -> false), Result.SUCCESS, Result.FAILURE
			));
		}

		return conditions;
	}
}
