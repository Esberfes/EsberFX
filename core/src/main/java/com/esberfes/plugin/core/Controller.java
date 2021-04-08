package com.esberfes.plugin.core;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private static Logger logger = LoggerFactory.getLogger(Controller.class);
    private static String[] PLUGIN_FOLDER = new String[]{"F:\\java\\EsberFX\\pluginimplelasticsearch\\target", "F:\\java\\EsberFX\\pluginimplcanvas\\target"};
    private static JarPluginManager jarPluginManager = new JarPluginManager(
            Arrays.stream(PLUGIN_FOLDER).map(Paths::get)
                    .collect(Collectors.toList()).toArray(new Path[0]));

    @FXML
    private TabPane tabPane;

    @FXML
    private TableView<PluginWrapper> pluginsInfo;

    private TrackableObservableList<PluginWrapper> pluginsPaths;
    private Map<String, List<Tab>> pluginNodes;
    private PropertyValueFactory<PluginWrapper, String> pluginWrapperStringPropertyValueFactory = new PropertyValueFactory<PluginWrapper, String>("selectedPluginExtensions") {
        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<PluginWrapper, String> param) {
            return selectedPluginExtensions;
        }
    };

    private Callback<TableColumn<PluginWrapper, Void>, TableCell<PluginWrapper, Void>> columnTableCellCallback = new Callback<TableColumn<PluginWrapper, Void>, TableCell<PluginWrapper, Void>>() {
        @Override
        public TableCell<PluginWrapper, Void> call(TableColumn<PluginWrapper, Void> param) {

            TableCell<PluginWrapper, Void> cell = new TableCell<PluginWrapper, Void>() {
                private final Button start = new Button("Start");
                private final Button stop = new Button("Stop");

                {
                    start.setOnAction((ActionEvent event) -> {
                        PluginWrapper pluginWrapper = ((PluginWrapper) getTableRow().getItem());
                        if (pluginWrapper != null) {
                            PluginWrapper pluginWrapperStarted = pluginsPaths.stream().filter(p -> p.getPluginId().equals(pluginWrapper.getPluginId()) && p.getPluginState() == PluginState.STARTED).findFirst().orElse(null);
                            if (pluginWrapperStarted != null) {
                                jarPluginManager.unloadPlugin(pluginWrapper.getPluginId());
                                pluginsPaths.remove(pluginWrapper);
                            } else {

                                jarPluginManager.startPlugin(pluginWrapper.getPluginId());
                                List<IExtensionPoint> extensions = jarPluginManager.getExtensions(IExtensionPoint.class, pluginWrapper.getPluginId());
                                for (IExtensionPoint iExtensionPoint : extensions) {
                                    Tab pluginTab = new Tab(pluginWrapper.getPluginId(), iExtensionPoint.onLoad());
                                    pluginTab.setClosable(false);
                                    List<Tab> nodes = pluginNodes.computeIfAbsent(pluginWrapper.getPluginId(), v -> new ArrayList<>());
                                    nodes.add(pluginTab);
                                    tabPane.getTabs().add(pluginTab);
                                }
                            }
                            pluginsInfo.refresh();
                        }
                    });
                    stop.setOnAction((ActionEvent event) -> {
                        PluginWrapper pluginWrapper = ((PluginWrapper) getTableRow().getItem());
                        if (pluginWrapper != null) {
                            PluginWrapper pluginWrapperStarted = pluginsPaths.stream().filter(p -> p.getPluginId().equals(pluginWrapper.getPluginId()) && p.getPluginState() == PluginState.STARTED).findFirst().orElse(null);
                            if (pluginWrapperStarted != null) {
                                try {
                                    jarPluginManager.disablePlugin(pluginWrapper.getPluginId());
                                    List<Tab> removed = pluginNodes.remove(pluginWrapper.getPluginId());
                                    if (removed != null) {
                                        List<Tab> newTabs = tabPane.getTabs().stream().filter(t -> !removed.contains(t)).collect(Collectors.toList());
                                        tabPane.getTabs().clear();
                                        tabPane.getTabs().addAll(newTabs);
                                    }
                                } catch (Throwable e) {
                                    logger.error(e.getMessage());
                                }

                            }
                            pluginsInfo.refresh();
                        }
                    });
                }


                // jarPluginManager.unloadPlugin()
                //    jarPluginManager.stopPlugin()
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new HBox(start, stop));
                    }
                }

            };

            return cell;
        }
    };
    private SimpleStringProperty selectedPluginExtensions = new SimpleStringProperty() {
        @Override
        public void set(String newValue) {
            super.set(newValue);
            pluginsInfo.refresh();
        }
    };

    @FXML
    public void initialize() {
        try {
            jarPluginManager.loadPlugins();
            pluginNodes = new HashMap<>();

            Arrays.stream(PLUGIN_FOLDER).map(Paths::get)
                    .forEach(p -> {
                        FileWatcher fileWatcher = null;
                        try {
                            fileWatcher = new FileWatcher(p, new FileHandler() {
                                @Override
                                public void handle(File file, WatchEvent.Kind<?> fileEvent) throws InterruptedException {
                                    try {
                                        pluginsPaths.add(jarPluginManager.getPlugin(jarPluginManager.loadPlugin(file.toPath())));

                                    } catch (Throwable e) {
                                        logger.error(e.getMessage());
                                    }
                                }
                            }, true, StandardWatchEventKinds.ENTRY_CREATE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Thread watcherThread = new Thread(fileWatcher);
                        watcherThread.start();
                    });

            pluginsPaths = new TrackableObservableList<PluginWrapper>(jarPluginManager.getPlugins()) {
                @Override
                protected void onChanged(ListChangeListener.Change c) {
                    pluginsInfo.refresh();
                }
            };

            pluginsInfo.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            pluginsInfo.getSelectionModel().setCellSelectionEnabled(false);
            pluginsInfo.setItems(pluginsPaths);
            pluginsInfo.refresh();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    public TrackableObservableList<PluginWrapper> getPluginsPaths() {
        return pluginsPaths;
    }

    public void setPluginsPaths(TrackableObservableList<PluginWrapper> pluginsPaths) {
        this.pluginsPaths = pluginsPaths;
    }

    public PropertyValueFactory<PluginWrapper, String> getPluginWrapperStringPropertyValueFactory() {
        return pluginWrapperStringPropertyValueFactory;
    }

    public void setPluginWrapperStringPropertyValueFactory(PropertyValueFactory<PluginWrapper, String> pluginWrapperStringPropertyValueFactory) {
        this.pluginWrapperStringPropertyValueFactory = pluginWrapperStringPropertyValueFactory;
    }

    public String getSelectedPluginExtensions() {
        return selectedPluginExtensions.get();
    }

    public ObservableValue<String> selectedPluginExtensionsProperty() {
        return selectedPluginExtensions;
    }

    public void setSelectedPluginExtensions(String selectedPluginExtensions) {
        this.selectedPluginExtensions.set(selectedPluginExtensions);
    }

    public Callback<TableColumn<PluginWrapper, Void>, TableCell<PluginWrapper, Void>> getColumnTableCellCallback() {
        return columnTableCellCallback;
    }

    public void setColumnTableCellCallback(Callback<TableColumn<PluginWrapper, Void>, TableCell<PluginWrapper, Void>> columnTableCellCallback) {
        this.columnTableCellCallback = columnTableCellCallback;
    }
}
