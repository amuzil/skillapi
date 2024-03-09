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

public class RadixPath implements INBTSerializable<CompoundTag> {

    private LinkedList<Pair<Form, List<ModifierData>>> activationPath;

    public RadixPath() {
        activationPath = new LinkedList<>();
    }

    public void addStep(Form activatedForm, List<ModifierData> modifierData) {
        activationPath.add(Pair.of(activatedForm, modifierData));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();

        ListTag listOfPairsTag = new ListTag();

        CompoundTag pairTag;
        for (int i = 0; i < activationPath.size(); i++) {
            Pair<Form, List<ModifierData>> formListPair = activationPath.get(i);
            Form activeForm = formListPair.first;
            List<ModifierData> modifierData = formListPair.second;

            pairTag = new CompoundTag();
            ListTag modifierDataListTag = new ListTag();

            modifierData.forEach(modifierDataInstance -> modifierDataListTag.add(modifierDataInstance.serializeNBT()));

            pairTag.putString("form", activeForm.name());
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
            if(pairTag instanceof CompoundTag pairTagCompound) {
                Pair<Form, List<ModifierData>> stepPair;
                Form formActivated = FormDataRegistry.getFormByName(pairTagCompound.getString("form"));
                ListTag modifiersListTag = (ListTag)pairTagCompound.get("modifiers");

                List<ModifierData> modifierData = new ArrayList<>();

                modifiersListTag.forEach(tag -> modifierData.add(ModifiersRegistry.fromCompoundTag(compoundTag)));

                stepPair = Pair.of(formActivated, modifierData);
                activationPath.add(stepPair);
            }
        });
    }
}
