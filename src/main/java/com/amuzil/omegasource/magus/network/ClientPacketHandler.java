package com.amuzil.omegasource.magus.network;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.packets.client_executed.RegisterModifierListenersPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.SkillTriggeredPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.UnregisterModifierListenersPacket;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

//this class is for handling packets specific to the clientside,
// otherwise any references to clientside only classes in packet handling causes us to crash.
public class ClientPacketHandler {
    public static boolean handlePacket(SkillTriggeredPacket packet, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
           //todo handle activating a skill on the clientside
        });

        return true;
    }

    public static boolean handlePacket(RegisterModifierListenersPacket packet, Supplier<NetworkEvent.Context> ctx) {
        LogManager.getLogger().info("REGISTERING MODIFIERS");
        ctx.get().enqueueWork(() -> {
            packet.modifierTypes.forEach(modifierType -> {
                LogManager.getLogger().info("REGISTERING MODIFIER: " + modifierType);
                Magus.inputModule.registerModifierListener(ModifiersRegistry.fromName(modifierType).listener(), packet.treeData);
            });
        });

        return true;
    }

    public static boolean handlePacket(UnregisterModifierListenersPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LogManager.getLogger().info("UNREGISTERING MODIFIERS");
            Magus.inputModule.unregisterModifiers();
        });

        return true;
    }
}