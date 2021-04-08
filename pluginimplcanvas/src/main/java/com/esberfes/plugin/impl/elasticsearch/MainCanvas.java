package com.esberfes.plugin.impl.canvas;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MainCanvas extends Canvas {

    private static Logger logger = LoggerFactory.getLogger(MainCanvas.class);
    private final GraphicsContext graphics = getGraphicsContext2D();
    private List<IRender> renders = new LinkedList<>();
    private AnimationTimer animationTimer;
    private Bounds bounds = getBoundsInLocal();
    private PropertyChangeSupport propertyChangeSupport;

    public MainCanvas(double width, double height) {
        super(width, height);
        AtomicLong lastTime = new AtomicLong();
        renders.add(new Point());
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - lastTime.get();
                //logger.info("Elapsed: " + elapsed);

                synchronized (MainCanvas.this) {
                    renders.forEach(r -> r.render(now, graphics));
                }

                lastTime.set(System.currentTimeMillis());
            }
        };

        animationTimer.start();

        setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
propertyChangeSupport.firePropertyChange(null);
            }
        });
    }

}
