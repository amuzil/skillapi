package com.amuzil.omegasource.skillapi.data.leaves;

import com.amuzil.omegasource.skillapi.data.RadixLeaf;
import javafx.scene.input.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class MouseMovementLeaf implements RadixLeaf<List<Point2D>> {
    java.util.List<Point2D> path;


    public void init() {
        path = new LinkedList<Point2D>();
        MinecraftForge.EVENT_BUS.addListener(this::listener);
    }

    @Override
    public void burn() {
        path.clear();
        MinecraftForge.EVENT_BUS.removeListener(this::listener);
    }

    @Override
    public void reset() {
        path.clear();
    }

    @Override
    public List<Point2D> measure() {
        return path;
    }

    //TODO: Find the right event
    //Dunno if this is the right event
    void listener(MouseEvent event) {
        path.add(new Point2D.Double(event.getX(), event.getY()));
    }

}
