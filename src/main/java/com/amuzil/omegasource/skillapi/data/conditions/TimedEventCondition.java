package com.amuzil.omegasource.skillapi.data.conditions;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public abstract class TimedEventCondition<E extends Event> extends EventCondition<E> {

    int duration;

    @Override
    public void register(Runnable success, Runnable expire) {
        super.register(success, expire);
        //Need to replace with mc server world
        //Schedule.schedule(duration, () -> expire.run());
        //TODO: Figure out how to incorporate success and expire into mc's runnable system
        Minecraft.getInstance().executeIfPossible(() -> {
            //Somehow check for time:
            if (duration < 1)
                success.run();
            else
                expire.run();
        });
    }
}
