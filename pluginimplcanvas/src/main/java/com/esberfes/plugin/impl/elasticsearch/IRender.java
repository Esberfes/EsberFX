package com.esberfes.plugin.impl.canvas;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public interface IRender {
    void render(long now, GraphicsContext graphics );
    Bounds getBounds();
}
