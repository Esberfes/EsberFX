package com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.ConnectionToolbar.EVT_CONNECTED;

public class ConnectButton extends JFXButton implements PropertyChangeListener {

    public ConnectButton(Runnable runnable) {
        setText("Connect");
        setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setOnMouseClicked(event -> runnable.run());
        setStyle("-jfx-button-type: RAISED;-fx-background-color: blue;-fx-text-fill: white;  ");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(EVT_CONNECTED)) {
            boolean connected = (boolean) evt.getNewValue();
            setText(connected ? "Disconnect" : "Connect");
        }
    }
}
