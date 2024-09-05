package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
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
            buf.writeUtf(condition.modID());
            buf.writeUtf(condition.name());
        }
    }

    public static ConditionActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String modID = buf.readUtf();
        String condition = buf.readUtf();
        // Need to add a way to store forms...
        return new ConditionActivatedPacket(ConditionRegistry.getConditionByName(modID, condition));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        RadixUtil.getLogger().debug("Condition: " + condition.name());
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().moveDown(condition);
        });
        return true;
    }
}
