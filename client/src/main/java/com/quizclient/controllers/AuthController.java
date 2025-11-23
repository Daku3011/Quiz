package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

public class AuthController {
    @FXML public TextField username;
    @FXML public PasswordField password;
    @FXML public Label message;

    @FXML
    public void onLogin(ActionEvent ev) {
        try {
            var body = Map.of("username", username.getText(), "password", password.getText());
            var req = ApiClient.jsonRequest("/api/auth/faculty/login")
                    .POST(HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = ApiClient.HTTP.send(req, BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                JsonNode j = ApiClient.MAPPER.readTree(resp.body());
                // store faculty id/displayName in system properties for demo
                System.setProperty("facultyId", j.get("facultyId").asText());
                System.setProperty("facultyName", j.has("displayName") ? j.get("displayName").asText() : username.getText());
                // open dashboard
                Stage s = (Stage) ((Node)ev.getSource()).getScene().getWindow();
                s.setScene(new Scene(FXMLLoader.load(getClass().getResource("/fxml/faculty_dashboard.fxml"))));
            } else {
                message.setText("Login failed: " + resp.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onRegister() {
        try {
            var body = Map.of("username", username.getText(), "password", password.getText(), "displayName", username.getText());
            var req = ApiClient.jsonRequest("/api/auth/faculty/register")
                    .POST(HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = ApiClient.HTTP.send(req, BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                message.setText("Registered. Now login.");
            } else {
                message.setText("Register failed: " + resp.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Error: " + e.getMessage());
        }
    }
}
