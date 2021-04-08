package com.esberfes.plugin.impl.elasticsearch.component.toolbar.data;

import com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.FakeDataButton;
import com.jfoenix.controls.JFXToolbar;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DataManagementToolbar extends JFXToolbar implements PropertyChangeListener {

    private static Logger logger = LoggerFactory.getLogger(DataManagementToolbar.class);

    private PropertyChangeSupport support;
    private ElementsPerPageChoiceBox elementsPerPageChoiceBox;
    private FakeDataButton fakeDataButton;

    public DataManagementToolbar(Runnable onFake) {
        prefWidth(Double.MAX_VALUE);
        prefHeight(80);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setPadding(new Insets(10));

        support = new PropertyChangeSupport(this);

        elementsPerPageChoiceBox = new ElementsPerPageChoiceBox();
        elementsPerPageChoiceBox.addPropertyChangeListener(this);

        fakeDataButton = new FakeDataButton();
        fakeDataButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onFake.run();
            }
        });

        setRightItems(elementsPerPageChoiceBox);
        setLeftItems(fakeDataButton);

        setStyle("-fx-padding: 15px;");
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        elementsPerPageChoiceBox.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        elementsPerPageChoiceBox.removePropertyChangeListener(pcl);
    }

    public Integer getElementsPerPage() {
        return elementsPerPageChoiceBox.getValue();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        logger.info("Event: " + evt.getPropertyName() + " value: " + String.valueOf(evt.getNewValue()));
    }
}
