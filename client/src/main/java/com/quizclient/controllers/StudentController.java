package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.quizclient.dto.StudentInfo;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.net.http.*;
import java.util.Map;

public class StudentController {
    @FXML public TextField nameField, enrollmentField;
    @FXML public Label message;

    @FXML
    public void onRegister() {
        try {
            var body = Map.of("name", nameField.getText(), "enrollment", enrollmentField.getText());
            var req = ApiClient.jsonRequest("/api/student/register")
                    .POST(HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = ApiClient.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                JsonNode j = ApiClient.MAPPER.readTree(resp.body());
                Long sid = j.get("studentId").asLong();
                // store student id locally (system property for demo)
                System.setProperty("studentId", sid.toString());
                System.setProperty("studentName", nameField.getText());
                System.setProperty("studentEnroll", enrollmentField.getText());
                message.setText("Registered (ID: " + sid + "). Now join session.");
                // go to join screen
                Stage s = (Stage) nameField.getScene().getWindow();
                s.setScene(new Scene(FXMLLoader.load(getClass().getResource("/fxml/student_join.fxml"))));
            } else {
                message.setText("Register failed: " + resp.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message.setText("Error: " + ex.getMessage());
        }
    }
}
