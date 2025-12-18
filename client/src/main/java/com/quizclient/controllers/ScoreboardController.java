package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.*;
import java.util.List;
import java.util.Map;
import java.io.IOException;

/**
 * Displays live or final scores for a specific session.
 * Includes auto-refresh polling and print functionality.
 */
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

    /**
     * Opens a native print dialog to print the scoreboard table.
     */
    @FXML
    public void onPrint() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Scoreboard");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(
                "scoreboard_" + (sessionIdField.getText().isEmpty() ? "session" : sessionIdField.getText()) + ".csv");

        java.io.File file = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // Header
                writer.println("Student Name,Enrollment,Set,Score,Submitted At");

                // Rows
                for (Map<String, Object> row : table.getItems()) {
                    String name = String.valueOf(row.get("studentName")).replace(",", " ");
                    String enroll = String.valueOf(row.get("enrollment")).replace(",", " ");
                    String set = String.valueOf(row.get("questionSet"));
                    String score = String.valueOf(row.get("score"));
                    String time = String.valueOf(row.get("submittedAt"));

                    writer.printf("%s,%s,%s,%s,%s%n", name, enroll, set, score, time);
                }

                new Alert(Alert.AlertType.INFORMATION, "Export successful! Saved to " + file.getAbsolutePath())
                        .showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
            }
        }
    }
}
