package com.esberfes.plugin.impl.elasticsearch.component.toolbar.data;

import com.esberfes.plugin.impl.elasticsearch.Person;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

import static com.esberfes.plugin.impl.elasticsearch.component.toolbar.data.tree.TreeBuilder.EVT_EDITED;

public class DataTable extends TableView<DataTable.DataTableElement> implements PropertyChangeListener {
    public static final String EVT_PAGE_COUNT = "EVT_PAGE_COUNT";

    private static Logger logger = LoggerFactory.getLogger(DataTable.class);

    private int limit;
    private int offset;
    private RestHighLevelClient client;
    private String index;
    private int pageCount;
    private String url;
    private PropertyChangeSupport support;

    public static class DataTableElement implements Map<String, Object> {
        private Map<String, Object> data;
        private String _id;


        public DataTableElement(Map<String, Object> data, String _id) {
            this.data = data;
            this._id = _id;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public String get_id() {
            return _id;
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public boolean isEmpty() {
            return data.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return data.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return data.containsKey(value);
        }

        @Override
        public Object get(Object key) {
            return data.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            return data.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return data.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            data.putAll(m);
        }

        @Override
        public void clear() {
            data.clear();
        }

        @Override
        public Set<String> keySet() {
            return data.keySet();
        }

        @Override
        public Collection<Object> values() {
            return data.values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return data.entrySet();
        }
    }

    public DataTable(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
        support = new PropertyChangeSupport(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DataPaginator.EVT_PAGINATION)) {
            offset = limit * (int) evt.getNewValue();
            showPage();
        } else if (evt.getPropertyName().equals(ElementsPerPageChoiceBox.EVT_ELEMENTS_PAGE)) {
            limit = (int) evt.getNewValue();
            showPage();
        } else if (evt.getPropertyName().equals(EVT_EDITED)) {
            JSONObject jsonObject = (JSONObject) evt.getNewValue();
            String id = jsonObject.getString("_id");
            jsonObject.remove("_id");
            save(id, jsonObject.toString());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean connect(String url, String index) {
        try {
            clear();

            this.index = index;
            this.url = url;

            logger.info("Connecting, url: " + url + " index: " + index);

            client = new RestHighLevelClient(RestClient
                    .builder(new HttpHost(url, 9200, "http")));

            Map<String, MappingMetaData> mappings = client.indices()
                    .getMapping(new GetMappingsRequest().indices(index), RequestOptions.DEFAULT)
                    .mappings();

            for (Map.Entry<String, MappingMetaData> entry : mappings.entrySet()) {
                Map<String, Object> properties = (Map<String, Object>) entry.getValue()
                        .getSourceAsMap().get("properties");
                for (String columnKey : properties.keySet()) {
                    TableColumn<DataTableElement, ?> mapTableColumn = new TableColumn<>(columnKey + " (" + entry.getKey() + ")");
                    mapTableColumn.setCellValueFactory(new MapValueFactory(columnKey));
                    getColumns().add(mapTableColumn);
                }
            }

            showPage();

            return true;

        } catch (Throwable e) {
            logger.error("Error connecting to ElasticSearch, url: " + url + ", index: " + index + ", error: " + e.getMessage());
            clear();
            return false;
        }
    }

    private void showPage() {
        try {
            if (this.index == null || this.url == null)
                return;

            logger.info("Searching in index: " + index + ", size: " + limit + ", from: " + offset);

            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(limit);
            searchSourceBuilder.from(offset);
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);
            client.searchAsync(searchRequest, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    Platform.runLater(() -> {
                        getItems().clear();
                        pageCount = Math.max((int) (searchResponse.getHits().totalHits / limit), 1);
                        support.firePropertyChange(EVT_PAGE_COUNT, 0, pageCount);
                        logger.info("Page count set to: " + pageCount + ", total: " + searchResponse.getHits().totalHits);
                        getItems().addAll(Arrays.stream(searchResponse
                                .getHits()
                                .getHits()).map(e -> new DataTableElement(e.getSourceAsMap(), e.getId()))
                                .collect(Collectors.toList()));

                    });
                }

                @Override
                public void onFailure(Exception e) {
                    logger.info("Error searching data: " + e.getMessage());
                    clear();
                }
            });
        } catch (Throwable e) {
        }
    }

    public void disconnect() {
        clear();
    }

    private void clear() {
        getItems().clear();
        getColumns().clear();
        support.firePropertyChange(EVT_PAGE_COUNT, pageCount, 0);
        this.offset = 0;
        this.url = null;
        this.index = null;
        this.pageCount = 0;
    }

    public void createFakeData() {
        for (int i = 0; i < 5; i++) {
            try {
                IndexRequest request = new IndexRequest("persons", "_doc", UUID.randomUUID().toString());
                request.source(new Gson().toJson(Person.fake(0)), XContentType.JSON);
                request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
                    @Override
                    public void onResponse(IndexResponse indexResponse) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                connect(url, index);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        logger.error("Fake error: " + e.getMessage());
                    }
                });
            } catch (Throwable e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void save(String id, String data) {
        try {
            IndexRequest request = new IndexRequest("persons", "_doc", id);
            request.source(data, XContentType.JSON);
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse updateResponse) {
                    logger.debug("Saved data with id:  " + id + ", response: " + updateResponse.toString() + ". Data: " + data);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showPage();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    logger.error("Error saving: " + e.getMessage());
                }
            });
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
    }

}
