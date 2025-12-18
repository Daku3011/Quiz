package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleLongProperty;

public class ActiveSessionsController {

    @FXML
    private TableView<Map<String, Object>> sessionsTable;
    @FXML
    private TableColumn<Map<String, Object>, Number> idCol;
    @FXML
    private TableColumn<Map<String, Object>, String> titleCol;
    @FXML
    private TableColumn<Map<String, Object>, String> startCol;
    @FXML
    private TableColumn<Map<String, Object>, String> otpCol;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleLongProperty(((Number) c.getValue().get("id")).longValue()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty((String) c.getValue().get("title")));
        startCol.setCellValueFactory(c -> {
            Object t = c.getValue().get("startTime");
            return new SimpleStringProperty(t != null ? t.toString() : "Not Started");
        });
        otpCol.setCellValueFactory(c -> new SimpleStringProperty((String) c.getValue().get("otp")));

        onRefresh();
    }

    @FXML
    public void onRefresh() {
        try {
            var req = ApiClient.jsonRequest("/api/session/active").GET().build();
            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                List<Map<String, Object>> sessions = ApiClient.MAPPER.readValue(resp.body(), new TypeReference<>() {
                });
                sessionsTable.getItems().setAll(sessions);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to load sessions").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onStopSession() {
        var selected = sessionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a session to stop").show();
            return;
        }

        Long id = ((Number) selected.get("id")).longValue();
        if (!new Alert(Alert.AlertType.CONFIRMATION, "Stop session " + id + "? Students will be disconnected.")
                .showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            return;
        }

        try {
            var req = ApiClient.jsonRequest("/api/session/" + id + "/stop")
                    .POST(java.net.http.HttpRequest.BodyPublishers.noBody()).build();
            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                new Alert(Alert.AlertType.INFORMATION, "Session stopped").showAndWait();
                onRefresh();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to stop session: " + resp.statusCode()).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }
}
