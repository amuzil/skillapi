package com.amuzil.omegasource.magus.skill.conditionals;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.MultiCondition;
import com.amuzil.omegasource.magus.radix.builders.InputConverter;

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

    public ConditionBuilder fromInputData(List<InputData> formExecutionInputs) {
        formExecutionInputs.forEach(inputData -> conditionList.addAll(InputConverter.buildPathFrom(inputData)));
        return this;
    }

    public ConditionBuilder fromInputData(InputData formExecutionInput) {
        conditionList.addAll(InputConverter.buildPathFrom(formExecutionInput));
        return this;
    }

    /**
     * @return A CombinationCondition combining all of the prequisite InputData
     * for an input group.
     */
    public LinkedList<Condition> build() {
        //Creates a copy of the list
        List<Condition> conditions = conditionList.stream().toList();

        //Resets the builder
        reset();

        if (conditions.size() == 0)
            return null;

        return new LinkedList<>(conditions);
    }

    public void reset() {
        conditionList = new LinkedList<>();
    }
}
