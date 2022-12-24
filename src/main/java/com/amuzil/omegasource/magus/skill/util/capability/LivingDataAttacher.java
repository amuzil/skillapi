package com.amuzil.omegasource.magus.skill.util.capability;

import com.amuzil.omegasource.magus.Magus;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        LivingDataProvider provider = new LivingDataProvider();

        event.addCapability(LivingDataProvider.IDENTIFIER, provider);
    }


    private static class LivingDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation("magus", "livingDataProvider");

        private final IData livingData = new LivingData();
        private final LazyOptional<IData> optionalData = LazyOptional.of(() -> livingData);


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
