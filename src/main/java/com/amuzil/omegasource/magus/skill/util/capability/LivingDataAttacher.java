package com.amuzil.omegasource.magus.skill.util.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LivingDataAttacher {
    private LivingDataAttacher() {
    }

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        final LivingDataProvider provider = new LivingDataProvider();

        event.addCapability(LivingDataProvider.IDENTIFIER, provider);
    }

    private static class LivingDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation("magus", "livingDataProvider");

        private final ILivingData livingData = new LivingData();
        private final LazyOptional<ILivingData> optionalData = LazyOptional.of(() -> livingData);


        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return Capabilities.LIVING_DATA.orEmpty(cap, this.optionalData);
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
