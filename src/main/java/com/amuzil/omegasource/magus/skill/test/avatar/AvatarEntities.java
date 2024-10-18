package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AvatarEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Magus.MOD_ID);

    public static final RegistryObject<EntityType<TestProjectileEntity>> TEST_PROJECTILE =
            ENTITY_TYPES.register("test_projectile", () -> EntityType.Builder.<TestProjectileEntity>of(TestProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("test_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
