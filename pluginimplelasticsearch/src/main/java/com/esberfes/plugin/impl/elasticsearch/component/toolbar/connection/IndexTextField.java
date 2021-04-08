package com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection;


import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.ConnectionToolbar.EVT_CONNECTED;

public class IndexTextField extends JFXTextField implements PropertyChangeListener {

    public IndexTextField() {
        setText("persons");
        setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(EVT_CONNECTED)) {
            boolean connected = (boolean) evt.getNewValue();
            this.setEditable(!connected);
        }
    }
}
