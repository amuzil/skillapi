package com.amuzil.omegasource.magus.radix.path;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.Condition.Result;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressedCondition;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyCombination;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyPermutation;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;
import net.minecraftforge.event.TickEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

public class PathBuilder {
	private static final Map<Class<?>, Function<Object, LinkedList<Condition>>> CONDITION_BUILDERS = new HashMap<>();

	static {
		/* Keys. */
		//TODO: Account for max delay
		registerBuilder(KeyInput.class, keyInput -> {
			// Minimum amount of ticks a key must be pressed for it to be considered a held condition.
			//TODO: Adjust these
			final int HELD_THRESHOLD = 3;
			final int TIMEOUT_THRESHOLD = 50;

			LinkedList<Condition> conditions = new LinkedList<>();

			// Any time less than this is just a key press.
			// TODO: Adjust timeout to be per node.
			conditions.add(keyInput.held() > HELD_THRESHOLD
				? new KeyHoldCondition(keyInput.key().getValue(), keyInput.held(), TIMEOUT_THRESHOLD)
				: new KeyPressCondition(keyInput.key().getValue(), TIMEOUT_THRESHOLD)
			);

			if (keyInput.minDelay() > 0) {
				//TODO: Fix this to account for "action keys".
				conditions.add(new TickTimedCondition(
					TickEvent.Type.CLIENT, TickEvent.Phase.START,
					keyInput.maxDelay(), Result.SUCCESS,
					new KeyPressedCondition(TIMEOUT_THRESHOLD), Result.FAILURE, Result.SUCCESS
				));
			}

			return conditions;
		});
		registerBuilder(KeyPermutation.class,
			permutation -> permutation.keys().stream().map(PathBuilder::buildPathFrom)
				.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll)
		);
		registerBuilder(KeyCombination.class,
			combination -> combination.keys().stream().map(PathBuilder::buildPathFrom)
				.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll)
		);

		/* Mouse */
//		registerBuilder(MouseInput.class,
//				mouseInput -> );
//		registerBuilder(MouseWheelInput.class,
//				mouseWheelInput -> );
	}

	@SuppressWarnings("unchecked")
	public static <T> void registerBuilder(Class<T> type, Function<T, LinkedList<Condition>> builder) {
		CONDITION_BUILDERS.put(type, (Function<Object, LinkedList<Condition>>) builder);
	}

	// TODO turn a linked list of conditions into a connected node tree.

	public static <T> LinkedList<Condition> buildPathFrom(T data) {
		return CONDITION_BUILDERS.get(data.getClass()).apply(data);
	}
}
