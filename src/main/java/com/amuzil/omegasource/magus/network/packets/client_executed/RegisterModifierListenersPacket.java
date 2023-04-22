package com.amuzil.omegasource.magus.network.packets.client_executed;

import com.amuzil.omegasource.magus.network.ClientPacketHandler;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegisterModifierListenersPacket implements MagusPacket {
    public CompoundTag treeData;
    public List<String> modifierTypes;

    public RegisterModifierListenersPacket(List<String> modifierTypes, CompoundTag treeData) {
        this.modifierTypes = modifierTypes;
        this.treeData = treeData;
    }

    public void toBytes(FriendlyByteBuf buf) {
        CompoundTag object = new CompoundTag();
        object.put("treeData", treeData);

        ListTag list = new ListTag();
        modifierTypes.forEach(modifierType -> list.add(StringTag.valueOf(modifierType)));

        object.put("modifierTypes", list);

        buf.writeNbt(object);
    }

    public static RegisterModifierListenersPacket fromBytes(FriendlyByteBuf buf) {
        CompoundTag object = buf.readNbt();

        List<String> modifierTypes = new ArrayList<>();

        ListTag list = (ListTag)object.get("modifierTypes");
        list.forEach(tag -> {
            modifierTypes.add(tag.getAsString());
        });

        CompoundTag treeData = object.getCompound("treeData");

        return new RegisterModifierListenersPacket(modifierTypes, treeData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        return ClientPacketHandler.handlePacket(this, ctx);
    }
}
