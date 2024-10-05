package com.amuzil.omegasource.magus.radix.builders;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.MultiCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;
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
        registerBuilder(KeyInput.class, keyInputs -> {
            LinkedList<Condition> conditions = new LinkedList<>();
            if (keyInputs instanceof List<?>)
                for (Object obj: (List<?>) keyInputs) {
                    KeyInput keyInput = (KeyInput) obj;
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
                }


            return conditions;
        });
        // TODO: Need to print these out and test how they work,
        // TODO: in order to finalise ConditionBuilder.java.
        registerBuilder(MultiKeyInput.class,
                multiKeyInputs -> {
                    LinkedList<Condition> multiConditions = new LinkedList<>();
                   for (Object obj : (List<?>) multiKeyInputs) {
                        MultiKeyInput permutation = (MultiKeyInput) obj;
                        // THis list isn't working.
                        List<Condition> conditions = new LinkedList<>();
                        for (KeyInput input : permutation.keys()) {
                            conditions.addAll(buildPathFrom(List.of(input)));
                        }

                            //= new LinkedList<>(permutation.keys().stream().map(InputConverter::buildPathFrom)
                                //.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll));

                        // Create a MultiCondition from the flattened conditions
                        MultiCondition multiCondition = ConditionBuilder.createMultiCondition(conditions);
                        multiCondition.register("multi_key_press", multiCondition::reset, multiCondition::reset);
                        multiConditions.add(multiCondition);
                    }

                    // Return a list containing the MultiCondition
                    return multiConditions;
                }
        );
        registerBuilder(ChainedKeyInput.class,
                chainedKeyInputs -> {
                    LinkedList<Condition> chained = new LinkedList<>();
                    for (Object obj : (List<?>) chainedKeyInputs) {
                        ChainedKeyInput combination = (ChainedKeyInput) obj;
                        List<Condition> conditions = new LinkedList<>(combination.keys().stream().map(InputConverter::buildPathFrom)
                                .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll));

                        for (Condition condition : conditions) {
                            if (condition instanceof MultiCondition)
                                chained.add(ConditionBuilder.createSequentialCondition((MultiCondition) condition));
                            else chained.add(ConditionBuilder.createSequentialCondition(condition));
                        }
                    }
                    // Return a list containing the ChainedCondition
                    return chained;
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
    public static <T> void registerBuilder(Class<T> type, Function<Object, LinkedList<Condition>> builder) {
        CONDITION_BUILDERS.put(type, (Function<Object, LinkedList<Condition>>) builder);
    }

    // TODO turn a linked list of conditions into a connected node tree.

    public static <T> LinkedList<Condition> buildPathFrom(T data) {
        return CONDITION_BUILDERS.get(data.getClass()).apply(data);
    }

    public static LinkedList<Condition> buildPathFrom(List<InputData> data) {
        if (data.isEmpty())
            return new LinkedList<>();
        Class<?> type = data.get(0).getClass();
        Function<Object, LinkedList<Condition>> function = CONDITION_BUILDERS.get(type);
        return function.apply(data);
    }
}
