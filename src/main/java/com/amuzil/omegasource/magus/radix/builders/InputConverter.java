package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.MultiCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.ChainedKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.MultiKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InputConverter {
    private static final Map<Class<?>, Function<Object, LinkedList<Condition>>> CONDITION_BUILDERS = new HashMap<>();

    static {
        // Minimum amount of ticks a key must be pressed for it to be considered a held condition.
        // This value is used in the condition class for how to check validity. Not used here.
        final int HELD_THRESHOLD = 3;
        // 50 by default
        final int TIMEOUT_THRESHOLD = 10;

        /* Keys. */
        //TODO: Account for max delay
        registerBuilder(KeyInput.class, keyInput -> {

            LinkedList<Condition> conditions = new LinkedList<>();

            // Any time less than this is just a key press.
            // TODO: Adjust timeout to be per node.
            // Use a configurable value to be our default timeout. If the condition should never time out,
            // dont add the threshold value.
            int timeout = TIMEOUT_THRESHOLD + keyInput.timeout();
            if (keyInput.timeout() < 0)
                timeout = keyInput.timeout();

            // Default is about 0 ticks.

            KeyHoldCondition keyPress = new KeyHoldCondition(keyInput.key().getValue(),
                    keyInput.held(), timeout, keyInput.release());
            // We can change these runnables later if need be.
            keyPress.register("key_press", keyPress::reset, keyPress::reset);
            conditions.add(keyPress);


            return conditions;
        });
        // TODO: Need to print these out and test how they work,
        // TODO: in order to finalise ConditionBuilder.java.
        registerBuilder(MultiKeyInput.class,
                permutation -> {
                    List<Condition> conditions = new LinkedList<>(permutation.keys().stream().map(InputConverter::buildPathFrom)
                            .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll));


                    // List of multiconditions
                    List<MultiCondition> allConditions = new LinkedList<>();
                    allConditions.add(ConditionBuilder.createMultiCondition(conditions));
                    return new LinkedList<>(allConditions);
                }
        );
        registerBuilder(ChainedKeyInput.class,
                combination -> {
                    LinkedList<Condition> allConditions = new LinkedList<>();
                    int minDelay = 0, maxDelay = 0;
                    for (MultiKeyInput multi : combination.keys()) {

                    }
                    return (LinkedList<Condition>) multiConditions;
                }
        );

        /* Mouse */
//		registerBuilder(MouseInput.class,
//				mouseInput -> );
        registerBuilder(MouseWheelInput.class,
                mouseWheelInput -> {

                    LinkedList<Condition> conditions = new LinkedList<>();
                    // Placeholder for now
                    //conditions.add(new KeyPressCondition(0, TIMEOUT_THRESHOLD));

                    return conditions;

                }
        );
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
