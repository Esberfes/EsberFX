package com.esberfes.plugin.impl.elasticsearch.component.toolbar.data;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class ElementsPerPageChoiceBox extends JFXComboBox<Integer> {

    private static Logger logger = LoggerFactory.getLogger(ElementsPerPageChoiceBox.class);

    private PropertyChangeSupport support;
    private Integer elementsPerPage;
    public static final String EVT_ELEMENTS_PAGE = "EVT_ELEMENTS_PAGE";

    public ElementsPerPageChoiceBox() {
        setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);

        elementsPerPage = 10;
        getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500);
        setValue(elementsPerPage);

        support = new PropertyChangeSupport(this);
        getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                support.firePropertyChange(EVT_ELEMENTS_PAGE, elementsPerPage, getItems().get((Integer) number2));
                logger.info("Change: " + getValue() + ", N1: " + number + ", N2: " + number2);
                elementsPerPage = getItems().get((Integer) number2);
            }
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public Integer getElementsPerPage() {
        return elementsPerPage;
    }
}
