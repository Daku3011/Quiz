package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class AdminDashboardController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Map<String, Object>> archiveTable;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colId;
    @FXML
    private TableColumn<Map<String, Object>, String> colSession;
    @FXML
    private TableColumn<Map<String, Object>, String> colStudent;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colScore;
    @FXML
    private TableColumn<Map<String, Object>, String> colDate;

    @FXML
    private CheckBox antiCheatingCheck;
    @FXML
    private TextField defCountField;

    @FXML
    public void initialize() {
        // Setup Columns
        colId.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>((Integer) data.getValue().get("id")));
        colSession.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().get("sessionId"))));
        colStudent.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("studentName")));
        colScore.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>((Integer) data.getValue().get("score")));
        colDate.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("submittedAt")));

        // Load initial data
        onRefreshArchives();
    }

    @FXML
    public void onRefreshArchives() {
        try {
            // Need to add this endpoint to backend
            var req = ApiClient.jsonRequest("/api/admin/submissions").GET().build();
            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                List<Map<String, Object>> data = ApiClient.MAPPER.readValue(resp.body(), new TypeReference<>() {
                });
                archiveTable.getItems().clear();
                archiveTable.getItems().addAll(data);
            } else {
                System.err.println("Failed to load archives: " + resp.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField facUsernameField;
    @FXML
    private TextField facPasswordField;

    @FXML
    public void onCreateFaculty() {
        String u = facUsernameField.getText();
        String p = facPasswordField.getText();
        if (u == null || u.isBlank() || p == null || p.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Username/Password required").showAndWait();
            return;
        }

        try {
            var body = Map.of("username", u, "password", p);
            var req = ApiClient.jsonRequest("/api/admin/faculty")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                new Alert(Alert.AlertType.INFORMATION, "Faculty created!").showAndWait();
                facUsernameField.clear();
                facPasswordField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error: " + resp.body()).showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Connection failed").showAndWait();
        }
    }

    @FXML
    public void onSaveSettings() {
        // Mock save settings logic
        boolean antiCheat = antiCheatingCheck.isSelected();
        String count = defCountField.getText();

        // In a real app, send this to backend. For now, show success.
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Settings Saved! (Mock)\nAnti-Cheating: " + antiCheat + "\nDefault Count: " + count);
        a.show();
    }

    @FXML
    public void onLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        stage.setScene(scene);
    }
}
