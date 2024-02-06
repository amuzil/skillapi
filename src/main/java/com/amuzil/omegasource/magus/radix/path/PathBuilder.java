package com.amuzil.omegasource.magus.radix.path;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.Condition.Result;
import com.amuzil.omegasource.magus.radix.condition.MultiCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.TickTimedCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyHoldCondition;
import com.amuzil.omegasource.magus.radix.condition.minecraft.forge.key.KeyPressedCondition;
import com.amuzil.omegasource.magus.skill.conditionals.ConditionBuilder;
import com.amuzil.omegasource.magus.skill.conditionals.key.ChainedKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.KeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.key.MultiKeyInput;
import com.amuzil.omegasource.magus.skill.conditionals.mouse.MouseWheelInput;
import net.minecraftforge.event.TickEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PathBuilder {
    private static final Map<Class<?>, Function<Object, LinkedList<Condition>>> CONDITION_BUILDERS = new HashMap<>();

    static {
        // Minimum amount of ticks a key must be pressed for it to be considered a held condition.
        //TODO: Adjust these
        final int HELD_THRESHOLD = 3;
        // 50 by default
        final int TIMEOUT_THRESHOLD = 60000000;

        /* Keys. */
        //TODO: Account for max delay
        registerBuilder(KeyInput.class, keyInput -> {

            LinkedList<Condition> conditions = new LinkedList<>();

            // Any time less than this is just a key press.
            // TODO: Adjust timeout to be per node.
            conditions.add(new KeyHoldCondition(keyInput.key().getValue(),
                    keyInput.held() + KeyHoldCondition.KEY_PRESS_TIMEOUT, TIMEOUT_THRESHOLD, keyInput.release()));

            return conditions;
        });
        // TODO: Need to print these out and test how they work,
        // TODO: in order to finalise ConditionBuilder.java.
        registerBuilder(MultiKeyInput.class,
                permutation -> {
                    List<Condition> conditions = new LinkedList<>(permutation.keys().stream().map(PathBuilder::buildPathFrom)
                            .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll));


                    // List of multiconditions
                    List<MultiCondition> allConditions = new LinkedList<>();
                    allConditions.add(ConditionBuilder.createMultiCondition(conditions));

                    MultiCondition timedCondition, releaseCondition;
                    Condition timed, release;

                    // Moved the time delay code from the input path to here so it is not combined.
                    for (int i = 0; i < conditions.size(); i++) {
                        KeyInput input = permutation.keys().get(i);
                        
                        if (input.minDelay() > 0) {
                            //TODO: Fix this to account for "action keys".
//                            timed = new TickTimedCondition(
//                                    TickEvent.Type.CLIENT, TickEvent.Phase.START,
//                                    input.maxDelay(), Result.SUCCESS,
//                                    new KeyPressedCondition(TIMEOUT_THRESHOLD), Result.FAILURE, Result.SUCCESS
//                            );
//                            timedCondition = ConditionBuilder.createMultiCondition(timed);
//                            allConditions.add(timedCondition);
                        }
                    }
                    return new LinkedList<>(allConditions);
                }
        );
        registerBuilder(ChainedKeyInput.class,
                combination -> combination.keys().stream().map(PathBuilder::buildPathFrom)
                        .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll)
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
