package com.amuzil.omegasource.magus.skill.util.traits.skilltraits;

import com.amuzil.omegasource.magus.skill.util.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;

/**
 * Supports an R, G, and B value. Designed for 0 - 1D, but you can use an int if you divide
 * by 255D.
 */
public class ColourTrait extends SkillTrait {

    private double r, g, b;

    public ColourTrait(double r, double g, double b, String name) {
        super(name);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColourTrait(int r, int g, int b, String name) {
        super(name);
        this.r = r / 255D;
        this.g = g / 255D;
        this.b = b / 255D;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble(getName() + "Red", r);
        tag.putDouble(getName() + "Green", g);
        tag.putDouble(getName() + "Blue", b);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        r = nbt.getDouble(getName() + "Red");
        g = nbt.getDouble(getName() + "Green");
        b = nbt.getDouble(getName() + "Blue");
    }

    public void setR(double r) {
        this.r = r;
        markDirty();
    }

    public void setG(double g) {
        this.g = g;
        markDirty();
    }

    public void setB(double b) {
        this.b = b;
        markDirty();
    }

    public void setRGB(double r, double g, double b) {
        setR(r);
        setG(g);
        setB(b);
    }

    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() { return b;}

    @Override
    public void reset() {
        super.reset();
        //Default colour is white.
        setRGB(1D, 1D, 1D);
    }
}
