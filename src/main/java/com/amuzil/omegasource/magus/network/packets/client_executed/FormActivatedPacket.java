package com.amuzil.omegasource.magus.network.packets.client_executed;

import com.amuzil.omegasource.magus.entity.ElementProjectile;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.elements.Element;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.skill.forms.Forms;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;
import java.util.function.Supplier;

import static com.amuzil.omegasource.magus.Magus.MOD_ID;


public class FormActivatedPacket implements MagusPacket {

    private final Form form;
    private final Element element;
    private final int entityId; // Entity ID to send back to client for FX

    public FormActivatedPacket(Form form, Element element, int entityId) {
        this.form = Objects.requireNonNullElseGet(form, Form::new);
        this.element = element;
        this.entityId = entityId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (form != null) {
            buf.writeUtf(form.name());
            buf.writeUtf(element.name());
            buf.writeInt(entityId);
        }
    }

    public static FormActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String formName = buf.readUtf();
        String elementName = buf.readUtf();
        int entityId = buf.readInt();
        Form form = Registries.FORMS.get().getValue(new ResourceLocation(MOD_ID, formName));
        Element element = (Element) Registries.SKILL_CATEGORIES.get().getValue(new ResourceLocation(MOD_ID, elementName));
        return new FormActivatedPacket(form, element, entityId);
    }

    // Client-side handler
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(Form form, int entityId) {
        // Perform client-side particle effect or other rendering logic here
        Player player = Minecraft.getInstance().player;
        assert player != null;
        ElementProjectile elementProjectile = (ElementProjectile) player.level.getEntity(entityId);
        /**
          NOTE: Need to ensure ElementProjectile's extra constructor args are set client-side.
          @see ElementProjectile#ElementProjectile(EntityType, Level) This gets called first and server-side only.
          Can't change this default constructor because it's needed to register entities. Add/use any extra args to Packet.
        */
        assert elementProjectile != null;
        elementProjectile.startEffect(form, player);
//        System.out.println("HANDLE CLIENT PACKET ---> " + form);
    }

    // Server-side handler
    public static void handleServerSide(Form form, Element element, ServerPlayer player) {
        // Perform server-side entity spawning and updating logic and fire Form Event here
        ServerLevel level = player.getLevel();
        // TODO - Create/perform certain entity updates based on form and element
        //      - All Skills/techniques should be determined and handled here
        ElementProjectile entity = ElementProjectile.createElementEntity(form, element, player, level);
        if (form == Forms.ARC) {
            entity.arc(1.5f, 1);
        } else {
            entity.shoot(player.getViewVector(1).x, player.getViewVector(1).y, player.getViewVector(1).z, 1, 1);
        }
        level.addFreshEntity(entity);
        FormActivatedPacket packet = new FormActivatedPacket(form, element, entity.getId());
//        Predicate<ServerPlayer> predicate = (serverPlayer) -> player.distanceToSqr(serverPlayer) < 2500;
//        for (ServerPlayer nearbyPlayer: level.getPlayers(predicate.and(LivingEntity::isAlive))) {
//            MagusNetwork.sendToClient(packet, nearbyPlayer);
//        } // Keep this in case we want a more specific client packet distribution filter

        // Distribute packet to clients within 500 blocks
        MagusNetwork.CHANNEL.send(PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(),
                        500, level.dimension())), packet);
//        System.out.println("HANDLE SERVER PACKET ---> " + form);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(form, entityId));
            } else {
                ServerPlayer player = ctx.get().getSender();
                assert player != null;
                handleServerSide(form, element, player);
            }
        });
        return true;
    }
}
