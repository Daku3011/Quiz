package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Handles the initial login screen.
 * Authenticates users against the backend and routes them to the appropriate
 * dashboard.
 */
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    /**
     * Attempts to log the user in.
     * Validates credentials and redirects based on role (ADMIN/FACULTY).
     * 
     * @param event The action event triggering this call
     */
    @FXML
    public void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showAlert("Error", "Please enter username and password.");
            return;
        }

        try {
            Map<String, String> creds = Map.of("username", username, "password", password);
            String json = ApiClient.MAPPER.writeValueAsString(creds);

            var req = ApiClient.jsonRequest("/api/auth/login")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();

            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                Map<String, Object> body = ApiClient.MAPPER.readValue(resp.body(), new TypeReference<>() {
                });
                String role = (String) body.get("role");

                if ("ADMIN".equals(role)) {
                    navigate(event, "/fxml/admin_dashboard.fxml");
                } else if ("FACULTY".equals(role)) {
                    navigate(event, "/fxml/faculty_dashboard.fxml");
                } else {
                    showAlert("Login Failed", "Unknown role: " + role);
                }
            } else {
                showAlert("Login Failed", "Invalid credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Connection failed: " + e.getMessage());
        }
    }

    @FXML
    public void onStudent(ActionEvent event) throws IOException {
        navigate(event, "/fxml/student_register.fxml");
    }

    private void navigate(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.ERROR, content).showAndWait();
    }
}