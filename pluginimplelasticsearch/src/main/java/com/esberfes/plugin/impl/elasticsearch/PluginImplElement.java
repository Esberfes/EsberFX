package com.esberfes.plugin.impl.elasticsearch;

import com.esberfes.plugin.core.IExtensionPoint;
import com.esberfes.plugin.core.PluginDescriptorElement;
import com.esberfes.plugin.impl.elasticsearch.component.toolbar.connection.ConnectionToolbar;
import com.esberfes.plugin.impl.elasticsearch.component.toolbar.data.DataManagementToolbar;
import com.esberfes.plugin.impl.elasticsearch.component.toolbar.data.DataPaginator;
import com.esberfes.plugin.impl.elasticsearch.component.toolbar.data.DataTable;
import com.esberfes.plugin.impl.elasticsearch.component.toolbar.data.tree.TreeBuilder;
import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.json.JSONObject;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@PluginDescriptorElement
public class PluginImplElement extends Plugin {

    private static Logger logger = LoggerFactory.getLogger(PluginImplElement.class);

    public PluginImplElement(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("Start");
    }

    @Extension
    public static class LayoutLoader implements IExtensionPoint {

        private DataTable tableView;
        private ConnectionToolbar toolBar;
        private DataManagementToolbar dataManagementToolbar;
        private DataPaginator pagination;
        private TreeTableView<Object> current;
        private TreeBuilder treeBuilder;

        private TrackableObservableList<Map<String, Object>> postMessages = new TrackableObservableList<Map<String, Object>>() {
            @Override
            protected void onChanged(ListChangeListener.Change<Map<String, Object>> c) {
                tableView.refresh();
            }
        };

        public LayoutLoader() {
            pagination = new DataPaginator();
            dataManagementToolbar = new DataManagementToolbar(() -> tableView.createFakeData());
            tableView = new DataTable(dataManagementToolbar.getElementsPerPage(), 0);
            tableView.prefWidth(Double.MAX_VALUE);
            tableView.prefHeight(Double.MAX_VALUE);
            dataManagementToolbar.addPropertyChangeListener(tableView);
            pagination.addPropertyChangeListener(tableView);
            tableView.addPropertyChangeListener(pagination);

            toolBar = new ConnectionToolbar((url, index) -> tableView.connect(url, index), () -> tableView.disconnect());
            toolBar.addPropertyChangeListener(pagination);

            current = new TreeTableView<>();
            treeBuilder = new TreeBuilder(current);
            treeBuilder.addPropertyChangeListener(tableView);
            current.setEditable(true);
            current.setShowRoot(false);
            current.prefWidth(Double.MAX_VALUE);
            current.prefHeight(Double.MAX_VALUE);

            tableView.getSelectionModel().setCellSelectionEnabled(true);
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    TreeItem<Object> rootJson = treeBuilder.constructRoot(new JSONObject(newSelection.getData()).put("_id", newSelection.get_id()));
                    current.setRoot(rootJson);
                    current.refresh();
                }
            });
        }

        @Override
        public Node onLoad() {
            return buildMainWrapper();
        }

        private Node buildMainWrapper(Node... children) {
            GridPane gridPane = new GridPane();
            gridPane.setMinHeight(Control.USE_PREF_SIZE);
            gridPane.setMinWidth(Control.USE_PREF_SIZE);
            gridPane.setPrefWidth(1920);
            gridPane.setPrefHeight(1080);
            gridPane.setMaxHeight(Control.USE_PREF_SIZE);
            gridPane.setMaxWidth(Control.USE_PREF_SIZE);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100);
            gridPane.getColumnConstraints().add(columnConstraints);

            gridPane.add(toolBar, 0, 0);
            gridPane.add(dataManagementToolbar, 0, 1);
            gridPane.add(tableView, 0, 2);
            gridPane.add(pagination, 0, 3);
            gridPane.add(current, 0, 4);

            return gridPane;
        }
    }
}
