package com.amuzil.omegasource.magus.network;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.packets.client_executed.SkillTriggeredPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.FormActivatedPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.StartBendingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class MagusNetwork {
    private static final String PROTOCOL_VERSION = "1.0.0";
    private static int packetId = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Magus.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextID() {
        return packetId++;
    }

    public static void registerMessages() {
        CHANNEL.messageBuilder(FormActivatedPacket.class, nextID())
                .encoder(FormActivatedPacket::toBytes)
                .decoder(FormActivatedPacket::fromBytes)
                .consumerMainThread(FormActivatedPacket::handle)
                .add();

        CHANNEL.messageBuilder(SkillTriggeredPacket.class, nextID())
                .encoder(SkillTriggeredPacket::toBytes)
                .decoder(SkillTriggeredPacket::fromBytes)
                .consumerMainThread(SkillTriggeredPacket::handle)
                .add();

        CHANNEL.messageBuilder(StartBendingPacket.class, nextID())
                .encoder(StartBendingPacket::toBytes)
                .decoder(StartBendingPacket::fromBytes)
                .consumerMainThread(StartBendingPacket::handle)
                .add();
    }
}
