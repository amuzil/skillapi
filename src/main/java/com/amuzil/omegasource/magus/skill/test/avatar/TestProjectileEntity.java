package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;


public class TestProjectileEntity extends Projectile implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(TestProjectileEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(TestProjectileEntity.class, EntityDataSerializers.BYTE);
    private boolean leftOwner;
    private boolean hasBeenShot;
    private int life;

    public TestProjectileEntity(EntityType<TestProjectileEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public TestProjectileEntity(double x, double y, double z, Level level) {
        this(AvatarEntities.TEST_PROJECTILE.get(), level);
        this.setPos(x, y, z);
    }

    public TestProjectileEntity(LivingEntity livingEntity, Level level) {
        this(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(), level);
        this.setOwner(livingEntity);
    }

    public void projectileTick() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();
    }

    public void tick() {
        this.projectileTick();
        boolean flag = this.isNoPhysics();
        Vec3 deltaMovement = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double distance = deltaMovement.horizontalDistance();
            this.setYRot((float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(deltaMovement.y, distance) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for(AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        break;
                    }
                }
            }
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        Vec3 pos = this.position();
        Vec3 delta = pos.add(deltaMovement);
        HitResult hitresult = this.level.clip(new ClipContext(pos, delta, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) {
            delta = hitresult.getLocation();
        }

        while(!this.isRemoved()) {
            if (!this.level.isClientSide) {
                this.tickDespawn();
            }
            EntityHitResult entityhitresult = this.findHitEntity(pos, delta);
            if (entityhitresult != null) {
                hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                assert hitresult instanceof EntityHitResult;
                Entity entity = ((EntityHitResult)hitresult).getEntity();
                Entity owner = this.getOwner();
                if (entity instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)entity)) {
                    hitresult = null;
                    entityhitresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag) {
                if (net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
                    break;
                this.onHit(hitresult);
                this.hasImpulse = true;
                break;
            }

            if (entityhitresult == null) {
                break;
            }

            hitresult = null;
        }
        deltaMovement = this.getDeltaMovement();
        double d5 = deltaMovement.x;
        double d6 = deltaMovement.y;
        double d1 = deltaMovement.z;

        double d7 = this.getX() + d5;
        double d2 = this.getY() + d6;
        double d3 = this.getZ() + d1;
        double d4 = deltaMovement.horizontalDistance();
        if (flag) {
            this.setYRot((float)(Mth.atan2(-d5, -d1) * (double)(180F / (float)Math.PI)));
        } else {
            this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));
        }

        this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

        float f = 0.99F;
        this.setDeltaMovement(deltaMovement.scale((double)f));
        if (!this.isNoGravity() && !flag) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - (double)0.05F, vec34.z);
        }

        this.setPos(d7, d2, d3);
        this.checkInsideBlocks();
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
            System.out.println("ENTITY NEARBY: " + otherEntity);
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
//            if (entity != null) {
//                if (otherEntity instanceof TestProjectileEntity other) {
//                    System.out.println("THIS OWNER: " + entity + " | " + !entity.isPassengerOfSameVehicle(otherEntity));
//                    System.out.println("THAT OWNER: " + other.getOwner());
//                }
//            }
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(otherEntity);
        }
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 150) {
            System.out.println("BYE BYE BBY");
            this.discard();
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

    private boolean checkLeftOwner() {
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

    public void handleEntityEvent(byte data) {
        if (data == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(this.getParticle(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof Blaze) {
            if (this.getOwner() != null) {
                this.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y+0.5, entity.getViewVector(1).z, 0.75F, 1);
            }
        } else if (entity instanceof TestProjectileEntity) {
            System.out.println("SUCCESS MADE IT HIT!!!");
            if (this.getOwner() != null) {
                FX fx = FXHelper.getFX(new ResourceLocation(Magus.MOD_ID, "orb_bloom"));
                EntityEffect entityEffect = new EntityEffect(fx, level, entity);
                entityEffect.start();
                this.discard(); // kys asap
//                this.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y+0.5, entity.getViewVector(1).z, 0.75F, 1);
            }
        }  else {
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

    private ParticleOptions getParticle() {
        return ParticleTypes.FLAME;
    }

//    @Override
//    protected ItemStack getPickupItem() {
//        return PROJECTILE_ITEM;
//    }

    @Override
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }
}