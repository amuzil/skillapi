package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.ibm.icu.impl.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConditionPath implements INBTSerializable<CompoundTag> {

    public LinkedList<Pair<Condition, List<ModifierData>>> activationPath;
    public List<Condition> conditions;

    public ConditionPath() {
        activationPath = new LinkedList<>();
    }

    public ConditionPath(List<Condition> activatedConditions) {
        conditions = activatedConditions;
        activationPath = new LinkedList<>();
        List<ModifierData> emptyModifier = new ArrayList<>();
        for (Condition activatedCondition: activatedConditions) {
            activationPath.add(Pair.of(activatedCondition, emptyModifier));
        }
    }

    public void addStep(Condition activatedCondition, List<ModifierData> modifierData) {
        conditions.add(activatedCondition);
        activationPath.add(Pair.of(activatedCondition, modifierData));
    }

    @Override
    public String toString() {
        return conditions.toString();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();

        ListTag listOfPairsTag = new ListTag();

        CompoundTag pairTag;
        for (int i = 0; i < activationPath.size(); i++) {
            Pair<Condition, List<ModifierData>> conditionListPair = activationPath.get(i);
            Condition activeCondition = conditionListPair.first;
            List<ModifierData> modifierData = conditionListPair.second;

            pairTag = new CompoundTag();
            ListTag modifierDataListTag = new ListTag();

            modifierData.forEach(modifierDataInstance -> modifierDataListTag.add(modifierDataInstance.serializeNBT()));

            pairTag.putString("condition", activeCondition.toString());
            pairTag.put("modifiers", modifierDataListTag);
            listOfPairsTag.add(i, pairTag);
        }

        compoundTag.put("activationPath", listOfPairsTag);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        ListTag listOfPairsTag = (ListTag)compoundTag.get("activationPath");
        activationPath = new LinkedList<>();
        listOfPairsTag.forEach(pairTag -> {
            // Need to figure out how to convert this into conditions.
            // Going to go over capability data to check for every active listener and use those in the path.
            if(pairTag instanceof CompoundTag pairTagCompound) {
                Pair<Form, List<ModifierData>> stepPair;
                Form formActivated = FormDataRegistry.getFormByName(pairTagCompound.getString("form"));
                ListTag modifiersListTag = (ListTag)pairTagCompound.get("modifiers");

                List<ModifierData> modifierData = new ArrayList<>();

                modifiersListTag.forEach(tag -> modifierData.add(ModifiersRegistry.fromCompoundTag(compoundTag)));

                stepPair = Pair.of(formActivated, modifierData);
//                activationPath.add(stepPair);
            }
        });
    }
}
