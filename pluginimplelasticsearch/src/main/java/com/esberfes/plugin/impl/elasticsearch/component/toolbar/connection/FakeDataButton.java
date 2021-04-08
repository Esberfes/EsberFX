package com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Control;

public class FakeDataButton extends JFXButton {

    public FakeDataButton( ) {
        setText("Fake");
        setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setButtonType(ButtonType.RAISED);
        setStyle("-jfx-button-type: RAISED;-fx-background-color: blue;-fx-text-fill: white; ");
    }
}
