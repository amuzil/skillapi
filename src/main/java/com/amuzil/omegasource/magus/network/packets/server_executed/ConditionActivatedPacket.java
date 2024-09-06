package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.RadixUtil;
import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.registry.Registries;
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
        RadixUtil.getLogger().debug("Attempted Condition Pass ID: " + id);
        RadixUtil.getLogger().debug("Test Form Registry: " + Registries.FORMS.get().getValue(new ResourceLocation("magus:arc")));
        Condition cond = ConditionRegistry.getCondition(id);
        RadixUtil.getLogger().debug("Condition Passed: " + cond.name());
        return new ConditionActivatedPacket(cond);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        RadixUtil.getLogger().debug("Condition Activated By Packet: " + condition.name());
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            player.addDeltaMovement(new Vec3(0, 2, 0));
            // Intentional crashing because I want to know why my packet isn't being received correctly...
//            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().moveDown(condition);
            RadixUtil.getLogger().debug("Condition Activated By Packet: " + condition.name());
        });
        return true;
    }
}
