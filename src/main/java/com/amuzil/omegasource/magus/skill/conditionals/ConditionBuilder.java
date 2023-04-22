package com.amuzil.omegasource.magus.skill.conditionals;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.ChainedCondition;
import com.amuzil.omegasource.magus.radix.path.PathBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Covers all the different ways to activate an ability.
 * Gonna use an enum to describe for now. Will update that later; think of it as a placeholder.
 */
public class ConditionBuilder {

    private List<Condition> conditionList = new LinkedList<>();

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

    public Condition build() {
        if(conditionList.size() == 0) return null;
        if(conditionList.size() == 1) return conditionList.get(0);

        return new ChainedCondition(conditionList);
    }
}
