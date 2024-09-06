package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConditionActivatedPacket implements MagusPacket {

    private final Condition condition;

    public ConditionActivatedPacket(Condition condition) {
        this.condition = condition;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if(condition != null) {
            buf.writeResourceLocation(new ResourceLocation(condition.modID() + ":" + condition.name()));
        }
    }

    public static ConditionActivatedPacket fromBytes(FriendlyByteBuf buf) {
        // Need to add a way to store forms...
        return new ConditionActivatedPacket(Registries.CONDITIONS.get().getValue(buf.readResourceLocation()));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        RadixUtil.getLogger().debug("Condition Activated By Packet: " + condition.name());
        ctx.get().enqueueWork(() -> {
            // Intentional crashing because I want to know why my packet isn't being received correctly...
//            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().moveDown(condition);
            RadixUtil.getLogger().debug("Condition Activated By Packet: " + condition.name());
        });
        return true;
    }
}
