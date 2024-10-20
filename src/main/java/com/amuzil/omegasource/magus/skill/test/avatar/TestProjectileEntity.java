package com.amuzil.omegasource.magus.skill.test.avatar;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;


public class TestProjectileEntity extends AbstractArrow implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);

    public TestProjectileEntity(EntityType<TestProjectileEntity> type, Level level) {
        super(type, level);
    }

    public TestProjectileEntity(Level Level, LivingEntity livingEntity) {
        super(AvatarEntities.TEST_PROJECTILE.get(), livingEntity, Level);
    }

    public TestProjectileEntity(Level Level, double x, double y, double z) {
        super(AvatarEntities.TEST_PROJECTILE.get(), x, y, z, Level);
    }

    public void handleEntityEvent(byte data) {
        if (data == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(this.getParticle(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
//        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof Blaze) {
            if (this.getOwner() != null) {
                this.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y+0.5, entity.getViewVector(1).z, 0.75F, 1);
            }
        } else {
            int i = 15; // Deal 15 damage
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float)i);
        }
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

    @Override
    protected ItemStack getPickupItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }
}