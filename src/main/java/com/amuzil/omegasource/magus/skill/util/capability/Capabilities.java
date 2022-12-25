package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.skill.util.traits.DataTrait;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Magus.MOD_ID)
public class Capabilities {

    public static final Capability<IData> LIVING_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static List<DataTrait> dataTraits = new ArrayList<>();

    private Capabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IData.class);
    }

    public static void initialiseCaps() {
        LivingDataAttacher.init();
    }
}
