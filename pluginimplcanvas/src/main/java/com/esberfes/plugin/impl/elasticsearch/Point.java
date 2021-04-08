package com.esberfes.plugin.impl.canvas;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Point implements IRender {

    private Bounds bounds;

    public Point() {
        bounds = new BoundingBox(10, 10, 10, 10);

    }
    @Override
    public void render(long now, GraphicsContext graphics ) {
        graphics.beginPath();
        graphics.setFill(Color.GREEN);
        graphics.setStroke(Color.BLUE);
        graphics.setLineWidth(5);
        graphics.strokeLine(40, 10, 10, 40);
        graphics.fillOval(10, 60, 30, 30);
        graphics.strokeOval(60, 60, 30, 30);
        graphics.fillRoundRect(110, 60, 30, 30, 10, 10);
        graphics.strokeRoundRect(160, 60, 30, 30, 10, 10);
        graphics.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        graphics.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        graphics.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        graphics.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        graphics.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        graphics.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        graphics.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        graphics.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        graphics.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    @Override
    public Bounds getBounds() {
        return null;
    }
}
