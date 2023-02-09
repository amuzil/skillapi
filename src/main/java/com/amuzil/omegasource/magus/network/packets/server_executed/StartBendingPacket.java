package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartBendingPacket {
    public void toBytes(FriendlyByteBuf buf) {
    }

    public static StartBendingPacket fromBytes(FriendlyByteBuf buf) {
        return new StartBendingPacket();
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().start();
        });
        return true;
    }
}
