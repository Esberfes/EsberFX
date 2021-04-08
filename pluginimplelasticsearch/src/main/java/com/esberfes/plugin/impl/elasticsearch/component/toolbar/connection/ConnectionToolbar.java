package com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection;

import com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.ConnectButton;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.function.BiFunction;

public class ConnectionToolbar extends ToolBar implements Runnable {

    public static final String EVT_CONNECTED = "EVT_CONNECTED";

    private final ConnectButton connectButton;
    private final com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.UrlTextField urlTextField;
    private final com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.IndexTextField indexTextField;
    private final BiFunction<String, String, Boolean> onConnectHandler;
    private boolean connected;
    private final PropertyChangeSupport support;
    private final Runnable disconnect;

    public ConnectionToolbar(BiFunction<String, String, Boolean> onConnectHandler, Runnable disconnect) {
        this.urlTextField = new UrlTextField();
        this.indexTextField = new IndexTextField();
        this.connectButton = new ConnectButton(this);
        this.onConnectHandler = onConnectHandler;
        this.disconnect = disconnect;

        prefWidth(Double.MAX_VALUE);
        prefHeight(80);
        setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        HBox hBox = new HBox(urlTextField, indexTextField, connectButton);
        hBox.setSpacing(30);
        hBox.setPadding(new Insets(10));
        hBox.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        getItems().add(hBox);

        support = new PropertyChangeSupport(this);

        addPropertyChangeListener(connectButton);
        addPropertyChangeListener(urlTextField);
        addPropertyChangeListener(indexTextField);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    @Override
    public void run() {
        if (isConnected()) {
            this.setConnected(false);
            disconnect.run();
        } else
            this.setConnected(onConnectHandler.apply(urlTextField.getText(), indexTextField.getText()));
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        support.firePropertyChange(EVT_CONNECTED, this.connected, connected);
        this.connected = connected;
    }

    public String getIndex() {
        return indexTextField.getText();
    }

    public String getUrl() {
        return urlTextField.getText();
    }
}
