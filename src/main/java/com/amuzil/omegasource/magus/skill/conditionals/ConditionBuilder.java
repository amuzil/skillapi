package com.amuzil.omegasource.magus.skill.conditionals;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.ChainedCondition;
import com.amuzil.omegasource.magus.radix.condition.MultiCondition;
import com.amuzil.omegasource.magus.radix.path.PathBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Covers all the different ways to activate an ability.
 * Gonna use an enum to describe for now. Will update that later; think of it as a placeholder.
 */
public class ConditionBuilder {

    private static ConditionBuilder builder;
    private List<Condition> conditionList = new LinkedList<>();

    public ConditionBuilder() {
    }

    public static ConditionBuilder instance() {
        if (builder == null)
            builder = new ConditionBuilder();
        return builder;
    }

    // These methods are static because they are not building a Condition from data, they simply transform one type
    // of condition into another.
    public static MultiCondition createMultiCondition(Condition condition) {
        return new MultiCondition(condition);
    }

    public static MultiCondition createMultiCondition(List<Condition> condition) {
        return new MultiCondition(condition);
    }

    // This is designed for simple conditions/singular.
    public static ChainedCondition createSequentialCondition(Condition condition) {
        return new ChainedCondition(createMultiCondition(condition));
    }

    public static ChainedCondition createSequentialCondition(MultiCondition condition) {
        return new ChainedCondition(condition);
    }

    public ConditionBuilder fromInputData(List<InputData> formExecutionInputs) {
        formExecutionInputs.forEach(inputData -> conditionList.addAll(PathBuilder.buildPathFrom(inputData)));
        return this;
    }

    public ConditionBuilder fromInputData(InputData formExecutionInput) {
        conditionList.addAll(PathBuilder.buildPathFrom(formExecutionInput));
        return this;
    }

    /**
     * @return A CombinationCondition combining all of the prequisite InputData
     * for an input group.
     */
    public Condition build() {
        //Creates a copy of the list

        List<Condition> conditions = conditionList.stream().toList();

        //Resets the builder
        reset();

        if (conditions.size() == 0)
            return null;
        if (conditions.size() == 1)
            return conditions.get(0);

        return new ChainedCondition(conditions);
    }

    public void reset() {
        conditionList = new LinkedList<>();
    }
}
