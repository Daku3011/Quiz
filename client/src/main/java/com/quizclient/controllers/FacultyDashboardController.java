package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.quizclient.dto.QuestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.http.*;
import java.util.*;

public class FacultyDashboardController {
    @FXML
    public TextArea syllabusArea;
    @FXML
    public Spinner<Integer> countSpinner;
    @FXML
    public ListView<QuestionDTO> questionsList;
    @FXML
    public Label sessionInfo;

    private final HttpClient http = ApiClient.HTTP;

    @FXML
    public void initialize() {
        // default question count
        countSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5));
        questionsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(QuestionDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else
                    setText((getIndex() + 1) + ". " + item.getText());
            }
        });

        questionsList.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2)
                editSelectedQuestion();
        });
    }

    @FXML
    public void onGenerateQuestions() {
        String text = syllabusArea.getText();
        if (text == null || text.isBlank()) {
            alert("Paste syllabus first");
            return;
        }
        try {
            int count = countSpinner.getValue();
            @SuppressWarnings("unused")
            var body = Map.of("text", text, "count", String.valueOf(count));
            var req = ApiClient.jsonRequest("/api/syllabus/generate")
                    .POST(HttpRequest.BodyPublishers
                            .ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                List<QuestionDTO> list = ApiClient.MAPPER.readValue(resp.body(),
                        new TypeReference<List<QuestionDTO>>() {
                        });
                questionsList.getItems().clear();
                questionsList.getItems().addAll(list);
            } else {
                alert("AI error: " + resp.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Error: " + ex.getMessage());
        }
    }

    @FXML
    public void editSelectedQuestion() {
        QuestionDTO sel = questionsList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("Select a question");
            return;
        }
        // Simple edit dialog for question text
        TextInputDialog d = new TextInputDialog(sel.getText());
        d.setTitle("Edit Question");
        d.setHeaderText("Edit question text");
        var res = d.showAndWait();
        res.ifPresent(newText -> {
            sel.setText(newText);
            questionsList.refresh();
        });
    }

    private String currentSessionId;

    @FXML
    public void onStartSession() {
        List<Map<String, String>> qlist = new ArrayList<>();
        for (QuestionDTO q : questionsList.getItems()) {
            Map<String, String> m = new HashMap<>();
            m.put("text", q.getText());
            m.put("optionA", q.getOptionA() == null ? "A" : q.getOptionA());
            m.put("optionB", q.getOptionB() == null ? "B" : q.getOptionB());
            m.put("optionC", q.getOptionC() == null ? "C" : q.getOptionC());
            m.put("optionD", q.getOptionD() == null ? "D" : q.getOptionD());
            m.put("correct", q.getCorrect() == null ? "A" : q.getCorrect());
            qlist.add(m);
        }
        try {
            var body = Map.of("title", "Quiz by " + System.getProperty("facultyName", "Faculty"), "questions", qlist);
            var req = ApiClient.jsonRequest("/api/session/start")
                    .POST(HttpRequest.BodyPublishers.ofString(ApiClient.MAPPER.writeValueAsString(body)))
                    .build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                JsonNode j = ApiClient.MAPPER.readTree(resp.body());
                String sid = j.get("sessionId").asText();
                String otp = j.get("otp").asText();
                sessionInfo.setText("Session ID: " + sid + "   OTP: " + otp);
                this.currentSessionId = sid;

                // Open scoreboard automatically
                openScoreboard(sid);
            } else {
                alert("Start failed: " + resp.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Error: " + ex.getMessage());
        }
    }

    @FXML
    public void onViewScoreboard() {
        openScoreboard(this.currentSessionId);
    }

    private void openScoreboard(String sid) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/scoreboard.fxml"));
            javafx.scene.Parent root = loader.load();

            if (sid != null) {
                ScoreboardController sc = loader.getController();
                sc.setSessionId(sid);
            }

            javafx.stage.Stage s = new javafx.stage.Stage();
            s.setScene(new javafx.scene.Scene(root));
            s.setTitle(sid != null ? "Scoreboard - Session " + sid : "Scoreboard");
            s.show();
        } catch (Exception e) {
            e.printStackTrace();
            alert("Failed to open scoreboard: " + e.getMessage());
        }
    }

    @FXML
    public void onBrowse(ActionEvent event) {
        // File chooser for uploading syllabus
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Syllabus File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF or Text", "*.pdf", "*.txt"));

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = "";
                if (file.getName().toLowerCase().endsWith(".pdf")) {
                    try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument
                            .load(file)) {
                        org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                        content = stripper.getText(document);
                    }
                } else {
                    // Assume text
                    try {
                        content = java.nio.file.Files.readString(file.toPath());
                    } catch (java.nio.charset.MalformedInputException e) {
                        // Fallback to ISO-8859-1 if UTF-8 fails
                        content = java.nio.file.Files.readString(file.toPath(),
                                java.nio.charset.StandardCharsets.ISO_8859_1);
                    }
                }
                syllabusArea.setText(content);
            } catch (Exception e) {
                e.printStackTrace();
                alert("Failed to read file: " + e.getMessage());
            }
        }
    }

    private void alert(String s) {
        new Alert(Alert.AlertType.INFORMATION, s, ButtonType.OK).showAndWait();
    }
}
