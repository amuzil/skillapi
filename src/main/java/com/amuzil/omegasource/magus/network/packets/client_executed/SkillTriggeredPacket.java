package com.amuzil.omegasource.magus.network.packets.client_executed;

import com.amuzil.omegasource.magus.network.ClientPacketHandler;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.skill.skill.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SkillTriggeredPacket implements MagusPacket {

    private final Skill skill;

    public SkillTriggeredPacket(Skill skill) {
        this.skill = skill;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if(skill != null) {
            //todo serialize skill
        }
    }

    public static SkillTriggeredPacket fromBytes(FriendlyByteBuf buf) {
        //todo deserialise skill
        return new SkillTriggeredPacket(null);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        return ClientPacketHandler.handlePacket(this, ctx);
    }
}
