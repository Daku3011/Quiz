package com.quizclient.controllers;

import com.quizclient.ApiClient;
import com.quizclient.dto.QuestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    public Spinner<Integer> setsSpinner;

    @FXML
    public void initialize() {
        // default question count
        countSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 60));
        setsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        questionsList.setCellFactory(lv -> new ListCell<>() {
            private final HBox content;
            private final Label label;
            private final Button editBtn;
            private final Button deleteBtn;

            {
                label = new Label();
                label.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);

                editBtn = new Button("Edit");
                editBtn.setOnAction(e -> {
                    questionsList.getSelectionModel().select(getItem());
                    editSelectedQuestion();
                });

                deleteBtn = new Button("Delete");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    questionsList.getItems().remove(getItem());
                });

                content = new HBox(10, label, editBtn, deleteBtn);
                content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(QuestionDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText((getIndex() + 1) + ". " + item.getText());
                    setGraphic(content);
                }
            }
        });

        questionsList.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2)
                editSelectedQuestion();
        });
    }

    @FXML
    public void onAddQuestion() {
        QuestionDTO newQ = new QuestionDTO();
        newQ.setText("New Question");
        newQ.setOptionA("Option A");
        newQ.setOptionB("Option B");
        newQ.setOptionC("Option C");
        newQ.setOptionD("Option D");
        newQ.setCorrect("A");
        newQ.setExplanation("Explanation...");
        questionsList.getItems().add(newQ);
        questionsList.getSelectionModel().select(newQ);
        editSelectedQuestion();
    }

    @FXML
    public Button generateBtn;
    @FXML
    public ProgressIndicator loadingIndicator;
    @FXML
    public Button cancelBtn;

    private javafx.concurrent.Task<List<QuestionDTO>> generationTask;

    @FXML
    public void onGenerateQuestions() {
        String text = syllabusArea.getText();
        if (text == null || text.isBlank()) {
            alert("Paste syllabus first");
            return;
        }

        // Disable UI & Show Loading
        setLoadingState(true);

        int count = countSpinner.getValue();

        // Run in background
        generationTask = new javafx.concurrent.Task<>() {
            @Override
            protected List<QuestionDTO> call() throws Exception {
                // Check cancellation
                if (isCancelled())
                    return null;

                @SuppressWarnings("unused")
                var body = Map.of("text", text, "count", String.valueOf(count));

                // Create request
                var req = ApiClient.jsonRequest("/api/syllabus/generate")
                        .POST(HttpRequest.BodyPublishers
                                .ofString(ApiClient.MAPPER.writeValueAsString(body)))
                        .timeout(java.time.Duration.ofSeconds(600)) // Increased timeout
                        .build();

                // Use synchronous send but it's interruptible
                var resp = http.send(req, HttpResponse.BodyHandlers.ofString());

                if (isCancelled())
                    return null;

                if (resp.statusCode() == 200) {
                    return ApiClient.MAPPER.readValue(resp.body(), new TypeReference<List<QuestionDTO>>() {
                    });
                } else {
                    throw new RuntimeException("AI error: " + resp.body());
                }
            }
        };

        generationTask.setOnSucceeded(e -> {
            List<QuestionDTO> result = generationTask.getValue();
            if (result != null) {
                questionsList.getItems().addAll(result);
                alert("Questions generated successfully!");
            }
            setLoadingState(false);
        });

        generationTask.setOnFailed(e -> {
            Throwable ex = generationTask.getException();
            if (ex instanceof java.util.concurrent.CancellationException || generationTask.isCancelled()) {
                // Ignore if cancelled
            } else {
                ex.printStackTrace();
                alert("Error: " + ex.getMessage());
            }
            setLoadingState(false);
        });

        generationTask.setOnCancelled(e -> setLoadingState(false));

        new Thread(generationTask).start();
    }

    @FXML
    public void onCancelGeneration() {
        if (generationTask != null && !generationTask.isDone()) {
            generationTask.cancel(true); // Interrupt thread
            setLoadingState(false);
        }
    }

    private void setLoadingState(boolean loading) {
        syllabusArea.setDisable(loading);
        countSpinner.setDisable(loading);

        generateBtn.setVisible(!loading);
        generateBtn.setManaged(!loading);

        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);

        cancelBtn.setVisible(loading);
        cancelBtn.setManaged(loading);
    }

    @FXML
    public void editSelectedQuestion() {
        QuestionDTO sel = questionsList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("Select a question");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/question_editor.fxml"));
            DialogPane pane = loader.load();

            // Populate fields
            TextArea qText = (TextArea) pane.lookup("#qTextArea");
            TextField optA = (TextField) pane.lookup("#optAField");
            TextField optB = (TextField) pane.lookup("#optBField");
            TextField optC = (TextField) pane.lookup("#optCField");
            TextField optD = (TextField) pane.lookup("#optDField");
            @SuppressWarnings("unchecked")
            ComboBox<String> correctBox = (ComboBox<String>) pane.lookup("#correctBox");
            // Looking for explanation field in dialog? Maybe simpler to add prompt for now
            // or leave out of editor if dialog not updated yet.
            // The user didn't explicitly ask for explanation in editor, but good to have.
            // For now I'll just keep existing editor fields.

            qText.setText(sel.getText());
            optA.setText(sel.getOptionA());
            optB.setText(sel.getOptionB());
            optC.setText(sel.getOptionC());
            optD.setText(sel.getOptionD());

            correctBox.getItems().addAll("A", "B", "C", "D");
            correctBox.setValue(sel.getCorrect() != null ? sel.getCorrect() : "A");

            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setTitle("Edit Question");
            dialog.setDialogPane(pane);
            dialog.setResultConverter(btn -> btn == ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<Boolean> result = dialog.showAndWait();
            if (result.isPresent() && result.get()) {
                sel.setText(qText.getText());
                sel.setOptionA(optA.getText());
                sel.setOptionB(optB.getText());
                sel.setOptionC(optC.getText());
                sel.setOptionD(optD.getText());
                sel.setCorrect(correctBox.getValue());

                questionsList.refresh();
            }

        } catch (Exception e) {
            e.printStackTrace();
            alert("Error opening editor: " + e.getMessage());
        }
    }

    private String currentSessionId;

    @FXML
    private TextField startTimeField;
    @FXML
    private TextField durationField;

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
            m.put("explanation", q.getExplanation());
            qlist.add(m);
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("title", "Quiz by " + System.getProperty("facultyName", "Faculty"));
            body.put("questions", qlist);

            String st = startTimeField.getText();
            if (st != null && !st.isBlank())
                body.put("startTime", st);

            String dur = durationField.getText();
            if (dur != null && !dur.isBlank()) {
                try {
                    body.put("durationMinutes", Integer.parseInt(dur));
                } catch (NumberFormatException e) {
                    alert("Invalid duration");
                    return;
                }
            }

            Integer sets = setsSpinner.getValue();
            body.put("numberOfSets", sets != null ? sets : 1);

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
        // ... existing browse code ...
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

    @FXML
    public void onLogout(ActionEvent event) throws java.io.IOException {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        javafx.scene.Parent root = loader.load();
        javafx.scene.Scene scene = new javafx.scene.Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private void alert(String s) {
        new Alert(Alert.AlertType.INFORMATION, s, ButtonType.OK).showAndWait();
    }
}
