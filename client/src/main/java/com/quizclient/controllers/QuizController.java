package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.quizclient.dto.QuestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.http.*;
import java.util.*;

public class QuizController {
    @FXML
    public VBox joinSection, quizSection;
    @FXML
    public TextField sessionField, otpField;
    @FXML
    public Button joinBtn;
    @FXML
    public Label statusLabel;
    @FXML
    public Label qNumber;
    @FXML
    public Label qText;
    @FXML
    public RadioButton rA, rB, rC, rD;
    @FXML
    public ToggleGroup choices;
    @FXML
    public Button nextBtn, prevBtn, submitBtn;

    private List<QuestionDTO> questions = new ArrayList<>();
    private int index = 0;
    private final HttpClient http = ApiClient.HTTP;

    @FXML
    public void onJoin() {
        String sessionId = sessionField.getText();
        String otp = otpField.getText();
        String studentId = System.getProperty("studentId");
        if (studentId == null) {
            statusLabel.setText("Register first!");
            return;
        }
        try {
            var body = Map.of("sessionId", sessionId, "otp", otp);
            var req = ApiClient.jsonRequest("/api/session/join")
                    .POST(HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                statusLabel.setText("Join failed: " + resp.body());
                return;
            }
            // fetch questions
            var qreq = ApiClient.jsonRequest("/api/session/" + sessionId + "/questions").GET().build();
            var qresp = http.send(qreq, HttpResponse.BodyHandlers.ofString());
            if (qresp.statusCode() == 200) {
                questions = ApiClient.MAPPER.readValue(qresp.body(), new TypeReference<List<QuestionDTO>>() {
                });
                if (questions.isEmpty()) {
                    statusLabel.setText("No questions");
                    return;
                }
                index = 0;

                // Switch view
                joinSection.setVisible(false);
                joinSection.setManaged(false);
                quizSection.setVisible(true);
                quizSection.setManaged(true);

                showQuestion();
            } else
                statusLabel.setText("Failed to load questions: " + qresp.body());
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showQuestion() {
        QuestionDTO q = questions.get(index);
        qNumber.setText("Question " + (index + 1) + " / " + questions.size());
        qText.setText(q.getText());
        rA.setText("A) " + (q.getOptionA() == null ? "" : q.getOptionA()));
        rB.setText("B) " + (q.getOptionB() == null ? "" : q.getOptionB()));
        rC.setText("C) " + (q.getOptionC() == null ? "" : q.getOptionC()));
        rD.setText("D) " + (q.getOptionD() == null ? "" : q.getOptionD()));
        // reset radio and select previous if existed
        choices.selectToggle(null);
        if ("A".equalsIgnoreCase(q.getSelected()))
            choices.selectToggle(rA);
        if ("B".equalsIgnoreCase(q.getSelected()))
            choices.selectToggle(rB);
        if ("C".equalsIgnoreCase(q.getSelected()))
            choices.selectToggle(rC);
        if ("D".equalsIgnoreCase(q.getSelected()))
            choices.selectToggle(rD);
        prevBtn.setDisable(index == 0);
        nextBtn.setDisable(index == questions.size() - 1);
    }

    @FXML
    public void onNext() {
        saveSelected();
        if (index < questions.size() - 1)
            index++;
        showQuestion();
    }

    @FXML
    public void onPrev() {
        saveSelected();
        if (index > 0)
            index--;
        showQuestion();
    }

    private void saveSelected() {
        Toggle t = choices.getSelectedToggle();
        if (t == null) {
            questions.get(index).setSelected(null);
            return;
        }
        if (t == rA)
            questions.get(index).setSelected("A");
        if (t == rB)
            questions.get(index).setSelected("B");
        if (t == rC)
            questions.get(index).setSelected("C");
        if (t == rD)
            questions.get(index).setSelected("D");
    }

    @FXML
    public void onSubmit() {
        saveSelected();
        // build answers list
        String sessionId = sessionField.getText();
        String studentId = System.getProperty("studentId");
        if (studentId == null) {
            statusLabel.setText("Register first.");
            return;
        }
        try {
            List<Map<String, Object>> ans = new ArrayList<>();
            for (QuestionDTO q : questions) {
                if (q.getSelected() == null)
                    continue;
                ans.add(Map.of("questionId", q.getId(), "selectedOption", q.getSelected()));
            }
            var body = Map.of("sessionId", sessionId, "studentId", studentId, "answers", ans);
            var req = ApiClient.jsonRequest("/api/quiz/submit")
                    .POST(HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                var j = ApiClient.MAPPER.readTree(resp.body());
                int score = j.has("score") ? j.get("score").asInt() : -1;
                new Alert(Alert.AlertType.INFORMATION, "Quiz submitted. Your score: " + score, ButtonType.OK)
                        .showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Submit failed: " + resp.body(), ButtonType.OK).showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }
}
