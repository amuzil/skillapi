package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
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
        conditions = new LinkedList<>();
    }

    public ConditionPath(List<Condition> activatedConditions) {
        conditions = activatedConditions;
        activationPath = new LinkedList<>();
        List<ModifierData> emptyModifier = new ArrayList<>();
        for (Condition activatedCondition : activatedConditions) {
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

            // Have to fix this...
            pairTag.putInt("condition", ConditionRegistry.getID(activeCondition));
            pairTag.put("modifiers", modifierDataListTag);
            listOfPairsTag.add(i, pairTag);
        }

        compoundTag.put("activationPath", listOfPairsTag);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        ListTag listOfPairsTag = (ListTag) compoundTag.get("activationPath");
        activationPath = new LinkedList<>();
        if (listOfPairsTag == null)
            return;
        listOfPairsTag.forEach(pairTag -> {
            // Need to figure out how to convert this into conditions.
            // Going to go over capability data to check for every active listener and use those in the path.
            if (pairTag instanceof CompoundTag pairTagCompound) {
                Pair<Condition, List<ModifierData>> stepPair;
                Condition condition = ConditionRegistry.getCondition(((CompoundTag) pairTag).getInt("condition"));
                ListTag modifiersListTag = (ListTag) pairTagCompound.get("modifiers");

                List<ModifierData> modifierData = new ArrayList<>();
                // Only add modifier data if it's not null
                if (modifiersListTag != null)
                    modifiersListTag.forEach(tag -> modifierData.add(ModifiersRegistry.fromCompoundTag(compoundTag)));

                stepPair = Pair.of(condition, modifierData);
                activationPath.add(stepPair);
            }
        });
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Condition cond : conditions) {
            hash += cond.hashCode();
        }
        // Hashing involves size of the list, and then an arbitrarily large prime number; e.g 29.
        hash = hash % (conditions.size() * 29);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // This needs to be overridden to *ignore* modifiers. It needs to only check that each condition in each path matches
        // (using custom defined hashcodes/equals method for each).
        if (!(obj instanceof ConditionPath))
            return false;

        return hashCode() == obj.hashCode() && conditions.size() == ((ConditionPath) obj).conditions.size()
                && activationPath.size() == conditions.size();
    }
}
