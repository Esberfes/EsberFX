package com.esberfes.plugin.core;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.pf4j.ExtensionPoint;

public interface IExtensionPoint extends ExtensionPoint {
    Node onLoad();
}
