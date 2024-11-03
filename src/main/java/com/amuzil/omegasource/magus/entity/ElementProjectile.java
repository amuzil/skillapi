package com.amuzil.omegasource.magus.entity;

import com.amuzil.omegasource.magus.entity.collision.ElementCollision;
import com.amuzil.omegasource.magus.entity.projectile.AirProjectile;
import com.amuzil.omegasource.magus.entity.projectile.EarthProjectile;
import com.amuzil.omegasource.magus.entity.projectile.FireProjectile;
import com.amuzil.omegasource.magus.entity.projectile.WaterProjectile;
import com.amuzil.omegasource.magus.skill.elements.Element;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.amuzil.omegasource.magus.skill.test.avatar.AvatarFormRegistry.*;


public abstract class ElementProjectile extends Projectile implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(ElementProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(ElementProjectile.class, EntityDataSerializers.BYTE);
    protected boolean leftOwner;
    private boolean hasBeenShot;
    private int life;
    private int ttk = 100;
    public boolean arcActive = false;
    public Form form;

    public ElementProjectile(EntityType<? extends ElementProjectile> type, Level level) {
        super(type, level);
    }

    public ElementProjectile(EntityType<? extends ElementProjectile> entityType, double x, double y, double z, Level level, Form form) {
        this(entityType, level);
        this.setPos(x, y, z);
        this.setNoGravity(true);
        this.form = form;
    }

    public ElementProjectile(EntityType<? extends ElementProjectile> entityType, LivingEntity livingEntity, Level level, Form form) {
        this(entityType, livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(), level, form);
        this.setOwner(livingEntity);
    }

    public void tick() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pos, Vec3 delta) {
        return getEntityHitResult(this.level, this, pos, delta,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(2.0D),
                this::canHitEntity, 0.3F);
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Level level, Entity thisEntity, Vec3 pos, Vec3 delta, AABB thisAABB, Predicate<Entity> canBeHit, float scale) {
        double maxDist = Double.MAX_VALUE;
        Entity entity = null;

        for(Entity otherEntity : level.getEntities(thisEntity, thisAABB, canBeHit)) {
//            System.out.println("ENTITY NEARBY: " + otherEntity);
            AABB aabb = otherEntity.getBoundingBox().inflate(scale);
            Optional<Vec3> optional = aabb.clip(pos, delta);
            if (optional.isPresent()) {
                double dist = pos.distanceToSqr(optional.get());
                if (dist < maxDist) {
                    entity = otherEntity;
                    maxDist = dist;
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity);
    }

    public boolean canHitEntity(Entity otherEntity) {
        if (!otherEntity.canBeHitByProjectile()) {
            return false;
        } else {
            Entity entity = this.getOwner();
            if (entity != null) {
                if (otherEntity instanceof ElementProjectile other) {
//                    System.out.println("arcActive: " + other.arcActive);
                    if (other.arcActive)
                        return true;
                }
            }
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(otherEntity);
        }
    }

    public void setTimeToKill(int ticks) {
        this.ttk = ticks;
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= ttk) {
//            System.out.println("BYE BYE BBY");
            this.discard();
//            this.arcActive = false;
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_FLAGS, (byte)0);
        this.entityData.define(PIERCE_LEVEL, (byte)0);
    }

    public boolean checkLeftOwner() {
        Entity owner = this.getOwner();
        if (owner != null) {
            for(Entity entity1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (entity) -> {
                return !entity.isSpectator() && entity.isPickable();
            })) {
                if (entity1.getRootVehicle() == owner.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNoPhysics() {
        if (!this.level.isClientSide) {
            return this.noPhysics;
        } else {
            return (this.entityData.get(ID_FLAGS) & 2) != 0;
        }
    }

//    public void handleEntityEvent(byte data) {
//        if (data == 3) {
//            System.out.println("HANDLE ENTITY EVENT");
//            for(int i = 0; i < 8; ++i) {
//                this.level.addParticle(this.getParticle(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
//            }
//        }
//    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof Blaze) {
            if (this.getOwner() != null) {
                this.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y+0.5, entity.getViewVector(1).z, 0.75F, 1);
            }
        } else if (entity instanceof ElementProjectile testProjectileEntity) {
            if (this.getOwner() != null && this.level.isClientSide) {
                ElementProjectile collisionEntity = new ElementCollision(this.getX(), this.getY(), this.getZ(), level);
                collisionEntity.setTimeToKill(5);
                level.addFreshEntity(collisionEntity);
                EntityEffect entityEffect = new EntityEffect(orb_bloom, level, collisionEntity);
                entityEffect.start();
                System.out.println("SUCCESS COLLISION!!!");
                this.discard();
                testProjectileEntity.discard();
            }
        }  else {
            // TODO - Check if player entity has countered
            int i = 10; // Deal 10 damage
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float)i);
            this.discard();
        }
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
//        super.onHitBlock(blockHitResult);
        this.discard();
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
//            this.discard();
        }
    }

    public void arc(float scale, float offset) {
        this.arcActive = true;
        Entity owner = this.getOwner();
        assert owner != null;
        Vec3[] pose = new Vec3[]{owner.position(), this.getOwner().getLookAngle()};
        pose[1] = pose[1].scale((scale)).add((0), (this.getOwner().getEyeHeight()), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        this.setPos(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }

    public static ElementProjectile createElementEntity(Form form, Element element, ServerPlayer player, ServerLevel level) {
        return switch (element.type()) {
            case AIR -> new AirProjectile(player, level, form);
            case WATER -> new WaterProjectile(player, level, form);
            case EARTH -> new EarthProjectile(player, level, form);
            case FIRE -> new FireProjectile(player, level, form);
        };
    }

    // Method to start initial visual effect
    public void startEffect(Form form, Player player) {
        this.form = form; // NOTE: Need this to ensure form is set client-side before onHit event
        FX fx = null;
        if (form.name().equals("strike"))
            fx = fire_bloom;
        if (form.name().equals("force"))
            fx = blue_fire;
        if (form.name().equals("arc")) {
            // TODO - Make perma fire and allow it to be shootable. Also stop catching fire from damaging receiver
            fx = null;
            arcActive = true;
        }
        if (fx != null) {
            EntityEffect entityEffect = new EntityEffect(fx, level, this);
            entityEffect.start();
        }
    }

    public static List<Entity> getNearbyEntities(Entity entity, double blocksRadius) {
        AABB aabb = entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(blocksRadius);
        assert Minecraft.getInstance().level != null;
        return Minecraft.getInstance().level.getEntities(entity, aabb).stream().toList();
    }
}