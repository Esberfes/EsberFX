package com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.ConnectionToolbar.EVT_CONNECTED;

public class UrlTextField  extends JFXTextField implements PropertyChangeListener {

    public UrlTextField() {
        setText("192.168.1.45");
        setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setStyle(" ");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(EVT_CONNECTED)) {
            boolean connected = (boolean) evt.getNewValue();
            this.setEditable(!connected);
        }
    }
}
