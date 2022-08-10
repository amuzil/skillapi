package com.amuzil.omegasource.skillapi.data.leaves;

import com.amuzil.omegasource.skillapi.data.RadixLeaf;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.LinkedList;
import java.util.List;

public class MouseMovementLeaf implements RadixLeaf<List<Vec3>> {
    java.util.List<Vec3> path;


    public void init() {
        path = new LinkedList<>();
        MinecraftForge.EVENT_BUS.addListener(this::listener);
    }

    @Override
    public void burn() {
        path.clear();
        //MinecraftForge.EVENT_BUS.removeListener();
    }

    @Override
    public void reset() {
        path.clear();
    }

    @Override
    public List<Vec3> measure() {
        return path;
    }

    //TODO: Find the right event
    //Dunno if this is the right event
    void listener(LivingEvent.LivingUpdateEvent event) {
        Vec3 look = event.getEntityLiving().getLookAngle();
        //int x = InputConstants.
        path.add(look);
    }

    void listen(ScreenEvent.KeyboardCharTypedEvent event) {

    }

}
