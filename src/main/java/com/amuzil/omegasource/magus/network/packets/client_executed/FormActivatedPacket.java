package com.amuzil.omegasource.magus.network.packets.client_executed;

import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.api.MagusPacket;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.amuzil.omegasource.magus.entity.TestProjectileEntity;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import static com.amuzil.omegasource.magus.skill.test.avatar.AvatarFormRegistry.blue_fire;
import static com.amuzil.omegasource.magus.skill.test.avatar.AvatarFormRegistry.fire_bloom;


public class FormActivatedPacket implements MagusPacket {

    private final Form form;
    private final int entityId; // Entity ID to send back to client for FX

    public FormActivatedPacket(Form form, int entityId) {
        this.form = Objects.requireNonNullElseGet(form, Form::new);
        this.entityId = entityId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (form != null) {
            buf.writeUtf(form.name());
            buf.writeInt(entityId);
        }
    }

    public static FormActivatedPacket fromBytes(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        int entityId = buf.readInt();
        Form form = Registries.FORMS.get().getValue(new ResourceLocation(MOD_ID, name));
        return new FormActivatedPacket(form, entityId);
    }

    // Client-side handler
    @OnlyIn(Dist.CLIENT)
    private static void handleClientSide(Form form, int entityId) {
        // Perform client-side particle effect or other rendering logic here
        Player player = Minecraft.getInstance().player;
        assert player != null;
        Level level = player.level;
        FX fx = null;
        if (form.name().equals("strike"))
            fx = fire_bloom;
        if (form.name().equals("force"))
            fx = blue_fire;
        if (fx != null) {
            TestProjectileEntity entity = (TestProjectileEntity) player.level.getEntity(entityId);
            EntityEffect entityEffect = new EntityEffect(fx, level, entity);
            entityEffect.start();
            System.out.println("HANDLE CLIENT PACKET ---> " + form);
        }
    }

    // Server-side handler
    public static void handleServerSide(Form form, ServerPlayer player) {
        // Perform server-side entity spawning and updating logic and fire Form Event here
        ServerLevel level = player.getLevel();
        TestProjectileEntity entity = new TestProjectileEntity(player, level);
        entity.shoot(player.getViewVector(1).x, player.getViewVector(1).y, player.getViewVector(1).z, 1, 1);
        level.addFreshEntity(entity);
        FormActivatedPacket packet = new FormActivatedPacket(form, entity.getId());

//        Predicate<ServerPlayer> predicate = (serverPlayer) -> player.distanceToSqr(serverPlayer) < 2500;
//        for (ServerPlayer nearbyPlayer: level.getPlayers(predicate.and(LivingEntity::isAlive))) {
//            MagusNetwork.sendToClient(packet, nearbyPlayer);
//        } // Keep this in case we want a more specific client packet distribution filter

        MagusNetwork.CHANNEL.send(PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(),
                        500, level.dimension())), packet);
        System.out.println("HANDLE SERVER PACKET ---> " + form);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientSide(form, entityId));
            } else {
                ServerPlayer player = ctx.get().getSender();
                assert player != null;
                handleServerSide(form, player);
            }
        });
        return true;
    }
}
