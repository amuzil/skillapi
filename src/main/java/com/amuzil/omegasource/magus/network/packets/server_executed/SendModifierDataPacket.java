package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendModifierDataPacket {

    private ModifierData modifierData;

    public SendModifierDataPacket(ModifierData modifierData) {
        this.modifierData = modifierData;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if(modifierData != null) {
            buf.writeNbt(modifierData.serializeNBT());
        }
    }

    public static SendModifierDataPacket fromBytes(FriendlyByteBuf buf) {
        CompoundTag modifierTag = buf.readNbt();
        return new SendModifierDataPacket(ModifiersRegistry.fromCompoundTag(modifierTag));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().addModifierData(modifierData);
        });
        return true;
    }
}
