package com.amuzil.omegasource.magus.skill.modifiers.listeners;

import com.amuzil.omegasource.magus.skill.elements.Element;
import com.amuzil.omegasource.magus.skill.elements.Elements;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierData;
import com.amuzil.omegasource.magus.skill.modifiers.api.ModifierListener;
import com.amuzil.omegasource.magus.skill.modifiers.data.TargetModifierData;
import com.amuzil.omegasource.magus.skill.util.bending.BendingMaterialUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class TargetModifierListener extends ModifierListener<InputEvent.MouseButton> {
    private Vec3 lastTargetPosition;
    private Element activeElement;

    public TargetModifierListener() {
        this.modifierData = new TargetModifierData();
    }

    @Override
    public void setupListener(CompoundTag compoundTag) {
        this.activeElement = Elements.fromName(compoundTag.getString("activeElement"));
    }

    @Override
    public boolean shouldCollectModifierData(InputEvent.MouseButton event) {
        if(event instanceof InputEvent.MouseButton.Post && // prevents double activation(Pre- and Post-event firing)
                event.getButton() == InputConstants.MOUSE_BUTTON_MIDDLE && event.getAction() == InputConstants.PRESS) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            Vec3 vector3d = mc.player.getEyePosition(1.0F);
            Vec3 vector3d1 = mc.player.getViewVector(1.0F);
            double distance = 20; //todo max distance make this configurable
            Vec3 vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            List<TagKey<Block>> bendableMaterials = BendingMaterialUtil.getBendableMaterialsForElement(activeElement);
            BlockHitResult hitresult = mc.player.level.clip(new ClipContext(vector3d, vector3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                BlockPos locationHit = hitresult.getBlockPos();
                LogManager.getLogger().info("Position: " + locationHit);
                BlockState hitBlockState = player.level.getBlockState(new BlockPos(locationHit));
                if(!hitBlockState.isAir()
                        && (bendableMaterials.size() == 0 || bendableMaterials.stream().anyMatch(hitBlockState::is))) {
                    LogManager.getLogger().info("Blockstate: " + player.level.getBlockState(new BlockPos(locationHit)));
                    lastTargetPosition = new Vec3(locationHit.getX(), locationHit.getY(), locationHit.getZ());
                }
            }
        }

        return lastTargetPosition != null;
    }

    @Override
    public ModifierData collectModifierDataFromEvent(InputEvent.MouseButton event) {
        ArrayList<Vec3> list = new ArrayList<>();
        list.add(this.lastTargetPosition);
        TargetModifierData data = new TargetModifierData(list);
        this.lastTargetPosition = null;
        return data;
    }

    @Override
    public ModifierListener copy() {
        return new TargetModifierListener();
    }
}
