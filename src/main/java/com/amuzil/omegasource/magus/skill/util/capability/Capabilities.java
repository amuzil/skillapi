package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.util.traits.IDataTrait;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public class Capabilities {

    public static final Capability<ILivingData> LIVING_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static List<IDataTrait> dataTraits;

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ILivingData.class);
    }

    public static void initialiseRegistries() {
        dataTraits = (List<IDataTrait>) Registries.DATA_TRAITS.getValues();
    }

    private Capabilities() {
    }
}
