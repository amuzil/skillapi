package com.amuzil.omegasource.magus.skill.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class Capabilities {

    public static final Capability<LivingDataInterface> LIVING_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(LivingDataInterface.class);
    }

    private Capabilities() {
    }
}
