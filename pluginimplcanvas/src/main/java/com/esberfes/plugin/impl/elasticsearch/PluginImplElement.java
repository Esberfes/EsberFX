package com.esberfes.plugin.impl.canvas;

import com.esberfes.plugin.core.IExtensionPoint;
import com.esberfes.plugin.core.PluginDescriptorElement;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptorElement
public class PluginImplElement extends Plugin {

    private static Logger logger = LoggerFactory.getLogger(PluginImplElement.class);

    public PluginImplElement(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("Start");
    }

    @Extension
    public static class LayoutLoader implements IExtensionPoint {

        @Override
        public Node onLoad() {
            GridPane gridPane = new GridPane();
            gridPane.setMinHeight(Control.USE_PREF_SIZE);
            gridPane.setMinWidth(Control.USE_PREF_SIZE);
            gridPane.setPrefWidth(1920);
            gridPane.setPrefHeight(1080);
            gridPane.setMaxHeight(Control.USE_PREF_SIZE);
            gridPane.setMaxWidth(Control.USE_PREF_SIZE);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100);
            gridPane.getColumnConstraints().add(columnConstraints);

            gridPane.add(new MainCanvas(1980, 1020), 0, 0);

            return gridPane;
        }

    }
}
