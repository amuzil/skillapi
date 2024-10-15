package com.amuzil.omegasource;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MagusTest {

    @Test
    public void checkResource(){
        System.out.println("Executing Test...");
        ResourceLocation resource; FX fx;

        resource = new ResourceLocation("magus", "blue_fire");
        fx = FXHelper.getFX(resource);
        System.out.println("resource: " + resource.getNamespace());
        System.out.println("resource: " + resource.getPath());
        System.out.println("resource: " + resource.toDebugFileName());
        System.out.println("fx: " + fx);

        assertNotNull(fx, "FX object not found!");
    }
}
