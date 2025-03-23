package com.amuzil.omegasource.magus.skill.forms;


import com.amuzil.omegasource.magus.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ActiveForm {
    private Form form;
    private boolean active;

    public ActiveForm(Form form, boolean active) {
        this.form = form;
        this.active = active;
    }

    public Form form() {
        return form;
    }

    public boolean active() {
        return active;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Form", this.form.name());
        tag.putBoolean("Active", this.active());
        return tag;
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        form = Registries.FORMS.get().getValue(ResourceLocation.tryParse(compoundTag.getString("Form")));
        active = compoundTag.getBoolean("Active");
    }

    @Override
    public String toString() {
        return String.format("ActiveForm(%s, %b)", form.name(), active);
    }
}
