package com.amuzil.omegasource.magus.network.packets.server_executed;

import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.elements.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

import static com.amuzil.omegasource.magus.Magus.MOD_ID;


public class ElementActivatedPacket implements MagusPacket {

    private final Element element;
    private final int entityId; // Entity ID to send back to client for FX

    public ElementActivatedPacket(Element element, int entityId) {
        this.element = element;
        this.entityId = entityId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(element.name());
        buf.writeInt(entityId);
    }

    public static ElementActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String elementName = buf.readUtf();
        int entityId = buf.readInt();
        Element element = (Element) Registries.SKILL_CATEGORIES.get().getValue(new ResourceLocation(MOD_ID, elementName));
        return new ElementActivatedPacket(element, entityId);
    }

    // Client-side handler
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(Element element) {
        // Update player's active element client-side
        Player player = Minecraft.getInstance().player;
        assert player != null;
        InputModule.setDiscipline(element);
    }

    // Server-side handler
    public static void handleServerSide(Element element, int entityId, ServerPlayer serverPlayer) {
        // Update player's active element server-side
        ServerLevel level = serverPlayer.getLevel();
        ElementActivatedPacket packet = new ElementActivatedPacket(element, entityId);
        Entity entity = level.getEntity(entityId); // Retrieve the entity from the world by its ID
        if (entity instanceof ServerPlayer player) { // Check if the entity is a ServerPlayer
            // Send packet to client
            MagusNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(element));
            } else {
                ServerPlayer player = ctx.get().getSender();
                assert player != null;
                handleServerSide(element, entityId, player);
            }
        });
        return true;
    }
}
