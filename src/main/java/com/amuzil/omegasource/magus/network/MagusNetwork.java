package com.amuzil.omegasource.magus.network;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.RegisterModifierListenersPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.SkillTriggeredPacket;
import com.amuzil.omegasource.magus.network.packets.client_executed.UnregisterModifierListenersPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.ConditionActivatedPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.SendModifierDataPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.StartBendingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
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
        CHANNEL.messageBuilder(ConditionActivatedPacket.class, nextID())
                .encoder(ConditionActivatedPacket::toBytes)
                .decoder(ConditionActivatedPacket::fromBytes)
                .consumerMainThread(ConditionActivatedPacket::handle)
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

        CHANNEL.messageBuilder(SendModifierDataPacket.class, nextID())
                .encoder(SendModifierDataPacket::toBytes)
                .decoder(SendModifierDataPacket::fromBytes)
                .consumerMainThread(SendModifierDataPacket::handle)
                .add();

        CHANNEL.messageBuilder(RegisterModifierListenersPacket.class, nextID())
                .encoder(RegisterModifierListenersPacket::toBytes)
                .decoder(RegisterModifierListenersPacket::fromBytes)
                .consumerMainThread(RegisterModifierListenersPacket::handle)
                .add();

        CHANNEL.messageBuilder(UnregisterModifierListenersPacket.class, nextID())
                .encoder(UnregisterModifierListenersPacket::toBytes)
                .decoder(UnregisterModifierListenersPacket::fromBytes)
                .consumerMainThread(UnregisterModifierListenersPacket::handle)
                .add();
    }


    public static void sendToClient(MagusPacket packet, ServerPlayer player) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void  sendToServer(MagusPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
