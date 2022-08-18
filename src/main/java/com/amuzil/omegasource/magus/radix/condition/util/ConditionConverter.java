package com.amuzil.omegasource.magus.radix.condition.util;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.Condition.Result;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.EventCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressCondition;
import com.amuzil.omegasource.magus.skill.activateable.KeyCombination;
import com.amuzil.omegasource.magus.skill.activateable.KeyInput;
import com.amuzil.omegasource.magus.skill.activateable.KeyPermutation;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
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

	public static LinkedList<Condition> combinationToConditions(KeyCombination combination) {
		return combination.getKeys().stream().map(ConditionConverter::permutationToConditions)
			.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
	}

	public static LinkedList<Condition> permutationToConditions(KeyPermutation permutation) {
		// TODO ensure the order is preserved
		return permutation.getKeys().stream().map(ConditionConverter::keyToConditions)
			.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
	}

	public static LinkedList<Condition> keyToConditions(KeyInput key) {
		LinkedList<Condition> conditions = new LinkedList<>();

		// Any time less than this is just a key press.
		conditions.add(key.getHeld() > HELD_THRESHOLD
			? new KeyHoldCondition(key.getKey(), key.getHeld(), 50)
			: new KeyPressCondition(key.getKey(), 50)
		);

		if (key.getMinDelay() > 0) {
			//TODO: Fix this to account for "action keys".
			conditions.add(new TickTimedCondition(
				TickEvent.Type.CLIENT, TickEvent.Phase.START,
				key.getMinDelay(), Result.SUCCESS,
				new EventCondition<KeyInputEvent>(event -> false), Result.SUCCESS, Result.FAILURE
			));
		}

		return conditions;
	}
}
