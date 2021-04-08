package com.esberfes.plugin.impl.elasticsearch.component.toolbar.data;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Pagination;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.ConnectionToolbar.EVT_CONNECTED;

public class DataPaginator extends Pagination implements PropertyChangeListener {

    public static final int DEFAULT_MAX_PAGE_INDICATOR_COUNT = 10;
    public static final String EVT_PAGINATION = "EVT_PAGINATION";

    private PropertyChangeSupport support;

    public DataPaginator() {
        super(1, 0);
        support = new PropertyChangeSupport(this);
        currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!oldValue.equals(newValue))
                    support.firePropertyChange(EVT_PAGINATION, oldValue, newValue);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DataTable.EVT_PAGE_COUNT)) {
            if(getPageCount() != (Integer) evt.getNewValue()) {
                setPageCount((Integer) evt.getNewValue());
                reload();
            }
        } else if(evt.getPropertyName().equals(EVT_CONNECTED)) {
            // Disconnect
            if(!(boolean)evt.getNewValue()) {
                setPageCount(1);
                reload();
            }
        }
    }

    private void reload() {
        setPage(0);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setPage(int page) {
        support.firePropertyChange(EVT_PAGINATION, getCurrentPageIndex(), 0);
        setCurrentPageIndex(page);
    }
}
