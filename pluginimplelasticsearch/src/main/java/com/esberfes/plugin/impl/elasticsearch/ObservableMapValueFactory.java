package com.esberfes.plugin.impl.elasticsearch;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ObservableMapValueFactory<V> implements
        Callback<TableColumn.CellDataFeatures<ObservableMap, V>, ObservableValue<V>> {

    private final Object key;

    public ObservableMapValueFactory(Object key) {
        this.key = key;
    }


    @Override
    public ObservableValue<V> call(CellDataFeatures<ObservableMap, V> features) {
        final ObservableMap map = features.getValue();
        final ObjectProperty<V> property = new SimpleObjectProperty<V>((V) map.get(key));
        map.addListener(new MapChangeListener<Object, V>() {
            public void onChanged(Change<?, ? extends V> change) {
                if (key.equals(change.getKey())) {
                    property.set((V) map.get(key));
                }
            }
        });
        return property;
    }
}