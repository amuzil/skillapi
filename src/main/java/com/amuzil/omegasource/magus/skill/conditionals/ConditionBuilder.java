package com.amuzil.omegasource.magus.skill.conditionals;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.CombinationCondition;
import com.amuzil.omegasource.magus.radix.path.PathBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Covers all the different ways to activate an ability.
 * Gonna use an enum to describe for now. Will update that later; think of it as a placeholder.
 */
public class ConditionBuilder {

    private List<Condition> conditionList = new LinkedList<>();
    private static ConditionBuilder builder;

    public static ConditionBuilder instance() {
        if (builder == null)
            builder = new ConditionBuilder();
        return builder;
    }

    public ConditionBuilder() {
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
     *
     * @return A CombinationCondition combining all of the prequisite InputData
     * for an input group.
     */
    public Condition build() {
        //Creates a copy of the list

        List<Condition> conditions = conditionList.stream().toList();

        //Resets the builder
        reset();

        if(conditions.size() == 0)
            return null;
        if(conditions.size() == 1)
            return conditions.get(0);

        return new CombinationCondition(conditions);
    }

    public void reset() {
        conditionList = new LinkedList<>();
    }
}
