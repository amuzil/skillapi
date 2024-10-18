package com.amuzil.omegasource.magus.network.packets.client_executed;

import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.radix.Condition;
import com.amuzil.omegasource.magus.radix.condition.ConditionRegistry;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FormActivatedPacket implements MagusPacket {

    private final Form form;

    public FormActivatedPacket(com.amuzil.omegasource.magus.skill.forms.Form condition) {
        this.form = condition;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (form != null) {
            buf.writeUtf(form.name());
        }
    }

    public static FormActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        Form form = Registries.FORMS.get().getValue(new ResourceLocation(name));
        return new FormActivatedPacket(form);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Publish Form Event here
            Player player = ctx.get().getSender();
        });
        return true;
    }
}
