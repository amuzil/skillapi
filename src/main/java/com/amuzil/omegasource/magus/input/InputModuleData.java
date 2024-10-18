package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.radix.builders.InputConverter;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.HashMap;
import java.util.List;

/**
 * These exist per form.
 */
public class InputModuleData {
    private final HashMap<RadixTree.InputType, List<InputData>> inputs;
    private final HashMap<RadixTree.InputType, List<Condition>> conditions;

    public InputModuleData() {
        this.inputs = new HashMap<>();
        this.conditions = new HashMap<>();
    }

    public void addTypeConditions(RadixTree.InputType type, List<Condition> conditions) {
        if (!this.conditions.containsKey(type)) {
            this.conditions.put(type, conditions);
        } else {
            List<Condition> existingConditions = this.conditions.get(type);
            existingConditions.addAll(conditions);
            this.conditions.put(type, existingConditions);
        }
    }

    public void addTypeInputs(RadixTree.InputType type, List<InputData> inputs) {
        if (!this.inputs.containsKey(type)) {
            this.inputs.put(type, inputs);
        } else {
            List<InputData> existingInputs = this.inputs.get(type);
            existingInputs.addAll(this.inputs.get(type));
            this.inputs.put(type, existingInputs);
        }
    }

    /**
     * Automatically fills out the type conditions based on its corresponding inputs.
     * @param type What type of input module the inputs are for.
     */
    public void fillConditions(RadixTree.InputType type) {
        List<InputData> inputs = getInputs(type);
        List<Condition> conditions = InputConverter.buildPathFrom(inputs);
        addTypeConditions(type, conditions);
    }

    public HashMap<RadixTree.InputType, List<Condition>> getConditions() {
        return conditions;
    }

    public HashMap<RadixTree.InputType, List<InputData>> getInputs() {
        return inputs;
    }

    public List<Condition> getConditions(RadixTree.InputType type) {
        return getConditions().get(type);
    }

    public List<InputData> getInputs(RadixTree.InputType type) {
        return getInputs().get(type);
    }
}
