package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.FormDataRegistry;
import com.amuzil.omegasource.magus.skill.util.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FormActivatedPacket {

    private final Form form;

    public FormActivatedPacket(Form form) {
        this.form = form;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if(form != null) {
            buf.writeUtf(form.name());
        }
    }

    public static FormActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String form = buf.readUtf();
        return new FormActivatedPacket(FormDataRegistry.getFormByName(form));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            CapabilityHandler.getCapability(player, CapabilityHandler.LIVING_DATA).getTree().moveDown(form);
        });
        return true;
    }
}
