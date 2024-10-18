package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConditionActivatedPacket implements MagusPacket {

    private final Condition condition;

    public ConditionActivatedPacket(Condition condition) {
        this.condition = condition;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (condition != null) {
            buf.writeInt(ConditionRegistry.getID(condition));
        }
    }

    public static ConditionActivatedPacket fromBytes(FriendlyByteBuf buf) {
        int id = buf.readInt();
        Condition cond = ConditionRegistry.getCondition(id);
        return new ConditionActivatedPacket(cond);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            // Intentional crashing because I want to know why my packet isn't being received correctly...
            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().moveDown(condition);
        });
        return true;
    }
}
