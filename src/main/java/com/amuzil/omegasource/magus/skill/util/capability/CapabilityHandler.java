package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;
import com.amuzil.omegasource.magus.skill.util.capability.entity.LivingDataCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class CapabilityHandler {
    public static final Capability<Data> LIVING_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(Data.class);
    }

    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof LivingEntity) {
            //capabilities all living entities get.
            //TODO: Add requirement to check against a list of compatible entities.
            //E.g custom npcs, or specific mobs you want to be able to use Skills.
//            e.addCapability(LivingDataCapability.IDENTIFIER, new LivingDataCapability.LivingDataProvider());
            if (e.getObject() instanceof Player) {
                //capabilities just players get.
            }
        }
    }

    @Nullable
    public static <T> T getCapability(Entity entity, Capability<T> capability) {
        if (entity == null) return null;
        if (!entity.isAlive()) return null;
        return entity.getCapability(capability).isPresent() ? entity.getCapability(capability).orElseThrow(() -> new IllegalArgumentException("Lazy optional must not be empty")) : null;
    }

    public static void initialiseCaps() {
        //Prevents class loading exceptions
        LivingDataCapability.LivingDataProvider.init();
    }
}
