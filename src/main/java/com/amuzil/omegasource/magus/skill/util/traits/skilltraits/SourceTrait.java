package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockState;

/**
 * Huge trait class, stores the source block of the Skill.
 */
public class SourceTrait extends SkillTrait {

    //TODO: Change this to an IForgeBlockState and make it support modded blocks. Right now it only supports vanilla.
    private BlockState state;
    private BlockPos pos;

    //Note: If you want to know how long a usable blockstate has been selected, use another
    //TimedTrait.
    public SourceTrait(String name, BlockState state, BlockPos pos) {
        super(name);
        this.state = state;
        this.pos = pos;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(getName() + " State", Block.getId(state));
        tag.putIntArray(getName() + " Pos", new int[] {
                pos.getX(), pos.getY(), pos.getZ()
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        state = Block.stateById(nbt.getInt(getName() + " State"));
        int[] blockPos = nbt.getIntArray(getName() + " Pos");
        pos = new BlockPos(blockPos[0],  blockPos[1], blockPos[2]);
    }

    public void setState(BlockState state) {
        this.state = state;
        markDirty();
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
        markDirty();
    }

    public BlockState getState() {
        return state;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void reset() {
        super.reset();
        //Default source info is 0,0,0 with Air.
        setPos(BlockPos.ZERO);
        setState(Blocks.AIR.defaultBlockState());
    }
}
