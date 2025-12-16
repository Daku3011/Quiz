package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.*;
import java.util.List;
import java.util.Map;
import java.io.IOException;

public class ScoreboardController {
    @FXML
    public TextField sessionIdField;
    @FXML
    public TableView<Map<String, Object>> table;
    @FXML
    public TableColumn<Map<String, Object>, String> nameCol;
    @FXML
    public TableColumn<Map<String, Object>, String> enrollCol;
    @FXML
    public TableColumn<Map<String, Object>, String> setCol;
    @FXML
    public TableColumn<Map<String, Object>, Integer> scoreCol;
    @FXML
    public TableColumn<Map<String, Object>, String> timeCol;

    @FXML
    public void initialize() {
        // table uses map entries
        nameCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().get("studentName"))));
        enrollCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().get("enrollment"))));
        setCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().get("questionSet"))));
        scoreCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleIntegerProperty(((Number) c.getValue().get("score")).intValue())
                        .asObject());
        timeCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().get("submittedAt"))));
    }

    private String sessionId;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        sessionIdField.setText(sessionId);
        startPolling();
    }

    private void startPolling() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(5), e -> onLoad()));
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
        // Initial load
        onLoad();
    }

    @FXML
    public void onRefresh() {
        onLoad();
    }

    @FXML
    public void onLoad() {
        String sid = sessionIdField.getText();
        if (sid == null || sid.isBlank()) {
            if (sessionId != null)
                sid = sessionId;
            else
                return;
        }
        try {
            var req = ApiClient.jsonRequest("/api/session/" + sid + "/scoreboard").GET().build();
            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                List<Map<String, Object>> rows = ApiClient.MAPPER.readValue(resp.body(),
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                table.getItems().clear();
                table.getItems().addAll(rows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onBack(javafx.event.ActionEvent event) throws IOException {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/faculty_dashboard.fxml"));
        javafx.scene.Parent root = loader.load();
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
    }
}
