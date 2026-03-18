package com.amuzil.omegasource.magus.skill.event;

import com.amuzil.omegasource.magus.radix.RadixTree;
import com.amuzil.omegasource.magus.skill.forms.ActiveForm;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;

public class SkillEvent extends LivingEvent {

    private Skill skill;
    private List<ActiveForm> formPath;

    public SkillEvent(LivingEntity entity, List<ActiveForm> formPath, Skill skill) {
        super(entity);
        this.skill = skill;
        this.formPath = formPath;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public Skill getSkill() {
        return skill;
    }

    public List<ActiveForm>  getFormPath() {
        return formPath;
    }
}
