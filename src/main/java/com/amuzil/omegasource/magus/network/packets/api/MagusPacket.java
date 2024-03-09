package com.amuzil.omegasource.magus.network.packets.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface MagusPacket {
    void toBytes(FriendlyByteBuf buffer);
    static MagusPacket fromBytes(FriendlyByteBuf buffer) { return null; }
    boolean handle(Supplier<NetworkEvent.Context> context);
}
