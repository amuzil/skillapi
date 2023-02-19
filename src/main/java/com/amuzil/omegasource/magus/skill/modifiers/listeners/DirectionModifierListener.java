package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.DirectionModifierData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.apache.logging.log4j.LogManager;

import java.util.LinkedList;
import java.util.List;

public class DirectionModifierListener extends ModifierListener<LivingEvent.LivingTickEvent> {
    private final double motionThreshold = 0.08d;

    public DirectionModifierListener() {
        this.modifierData = new DirectionModifierData();
    }

    //this ModifierListener doesn't use any nested listeners
    @Override
    public void setupListener(CompoundTag compoundTag) {
    }

    @Override
    public boolean shouldCollectModifierData(LivingEvent.LivingTickEvent event) {
        if(!(event.getEntity() instanceof Player)) return false;
        if((event.getEntity().getDeltaMovement().length() > motionThreshold)) {
            LogManager.getLogger().info("MOVE MOTION: " + event.getEntity().getDeltaMovement().length());
            LogManager.getLogger().info("MOVE DIRECTION: " + event.getEntity().getMotionDirection());
            LogManager.getLogger().info(event.getEntity().getDeltaMovement());
            return true;
        }

        return false;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(LivingEvent.LivingTickEvent event) {
        List<Direction> directions = new LinkedList<>();
        directions.add(event.getEntity().getMotionDirection());
        return new DirectionModifierData(directions);
    }

    @Override
    public ModifierListener copy() {
        return new DirectionModifierListener();
    }
}
