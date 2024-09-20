package com.amuzil.omegasource.magus.input;

import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.conditionals.InputData;

import java.util.HashMap;
import java.util.List;

public class InputModuleData {
    private HashMap<RadixTree.InputType, List<InputData>> inputTypes;
    private HashMap<RadixTree.InputType, List<Condition>> conditionTypes;

    public InputModuleData() {
        this.inputTypes = new HashMap<>();
        this.conditionTypes = new HashMap<>();
    }

    public void addTypeConditions(RadixTree.InputType type, List<Condition> conditions) {
        if (!conditionTypes.containsKey(type)) {
            this.conditionTypes.put(type, conditions);
        }
        else {
            List<Condition> existingConditions = this.conditionTypes.get(type);
            existingConditions.addAll(conditions);
            this.conditionTypes.put(type, existingConditions);
        }
    }

    public void addTypeInputs(RadixTree.InputType type, List<InputData> inputs) {
        if (!inputTypes.containsKey(type)) {
            this.inputTypes.put(type, inputs);
        }
        else {
            List<InputData> existingInputs = this.inputTypes.get(type);
            existingInputs.addAll(inputTypes.get(type));
            this.inputTypes.put(type, existingInputs);
        }
    }


}
