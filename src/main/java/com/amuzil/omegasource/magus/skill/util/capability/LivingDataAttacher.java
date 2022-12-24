package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.Magus;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Magus.MOD_ID)
public class LivingDataAttacher {
    private LivingDataAttacher() {
    }
    public static void init() {
        //Prevents class loading exceptions
        LivingDataProvider.init();
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            //TODO: Add requirement to check against a list of compatible entities.
            //E.g custom npcs, or specific mobs you want to be able to use Skills.
            LivingDataProvider provider = new LivingDataProvider();
            event.addCapability(LivingDataProvider.IDENTIFIER, provider);
        }
    }


    private static class LivingDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        static void init() {
        }

        public static final ResourceLocation IDENTIFIER = new ResourceLocation("magus", "living_data_provider");

        private final IData livingData = new LivingData();
        private final LazyOptional<IData> optionalData = LazyOptional.of(() -> livingData);


        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
            return Capabilities.LIVING_DATA.orEmpty(cap, this.optionalData);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return getCapability(cap);
        }

        void invalidate() {
            this.optionalData.invalidate();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.livingData.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.livingData.deserializeNBT(nbt);
        }
    }
}
