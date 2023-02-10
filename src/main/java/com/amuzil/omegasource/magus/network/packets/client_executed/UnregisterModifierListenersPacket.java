package com.amuzil.omegasource.magus.network.packets.client_executed;

import com.amuzil.omegasource.magus.network.ClientPacketHandler;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnregisterModifierListenersPacket implements MagusPacket {

    public UnregisterModifierListenersPacket() {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static UnregisterModifierListenersPacket fromBytes(FriendlyByteBuf buf) {
        return new UnregisterModifierListenersPacket();
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        return ClientPacketHandler.handlePacket(this, ctx);
    }
}
