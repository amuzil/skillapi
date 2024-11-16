package com.amuzil.omegasource.magus.radix;

import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.ibm.icu.impl.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class ConditionPath implements INBTSerializable<CompoundTag> {

    public HashMap<Condition, List<ModifierData>> activationPath;
    public List<Condition> conditions;
    private boolean isDirty;

    public ConditionPath() {
        activationPath = new HashMap<>();
        conditions = new LinkedList<>();
        markDirty();
    }

    public ConditionPath(List<Condition> activatedConditions) {
        conditions = activatedConditions;
        if (conditions == null)
            conditions = new LinkedList<>();

        activationPath = new HashMap<>();
        List<ModifierData> emptyModifier = new ArrayList<>();
        
        for (Condition activatedCondition : conditions) {
            activationPath.put(activatedCondition, emptyModifier);
        }
        markDirty();
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public void markClean() {
        this.isDirty = false;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void addStep(Condition activatedCondition, List<ModifierData> modifierData) {
        if (activatedCondition != null) {
            conditions.add(activatedCondition);
            activationPath.put(activatedCondition, modifierData);
            markDirty();
        }
    }

    // Remember that modifier data goes up to the currently active node. So, a lot of Effects
    // will only check their first condition for the right modifier data.
    public List<ModifierData> getModifiers(Condition condition) {
        return activationPath.getOrDefault(condition, new ArrayList<>());
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
        int i = 0;
        for (Map.Entry<Condition, List<ModifierData>> pathEntry : activationPath.entrySet()) {
            Condition activeCondition = pathEntry.getKey();
            List<ModifierData> modifierData = pathEntry.getValue();

            pairTag = new CompoundTag();
            ListTag modifierDataListTag = new ListTag();

            modifierData.forEach(modifierDataInstance -> modifierDataListTag.add(modifierDataInstance.serializeNBT()));

            // Have to fix this...
            pairTag.putInt("condition", ConditionRegistry.getID(activeCondition));
            pairTag.put("modifiers", modifierDataListTag);
            listOfPairsTag.add(i, pairTag);
            i++;
        }

        compoundTag.put("activationPath", listOfPairsTag);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        ListTag listOfPairsTag = (ListTag) compoundTag.get("activationPath");
        activationPath = new HashMap<>();
        if (listOfPairsTag == null) return;
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

                activationPath.put(condition, modifierData);
            }
        });
        markClean();
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
        if (!(obj instanceof ConditionPath)) return false;

        return hashCode() == obj.hashCode() && conditions.size() == ((ConditionPath) obj).conditions.size() && activationPath.size() == conditions.size();
    }
}
