package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.skill.modifiers.ModifiersRegistry;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SendModifierDataPacket implements MagusPacket {

    private List<ModifierData> modifierData;

    public SendModifierDataPacket(List<ModifierData> modifierData) {
        this.modifierData = modifierData;
    }

    public void toBytes(FriendlyByteBuf buf) {
        CompoundTag data = new CompoundTag();
        ListTag listTag = new ListTag();
        if(modifierData != null && modifierData.size() > 0) {
            modifierData.forEach(modifier -> listTag.add(modifier.serializeNBT()));
        }
        data.put("modifiers", listTag);
        buf.writeNbt(data);
    }

    public static SendModifierDataPacket fromBytes(FriendlyByteBuf buf) {
        CompoundTag modifierTag = buf.readNbt();
        ListTag listTag = (ListTag) modifierTag.get("modifiers");
        List<ModifierData> modifiers = new ArrayList<>();
        listTag.forEach(tag -> modifiers.add(ModifiersRegistry.fromCompoundTag((CompoundTag) tag)));
        return new SendModifierDataPacket(modifiers);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().addModifierData(modifierData);
        });
        return true;
    }
}
