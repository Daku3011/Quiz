package com.quizclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class SyllabusEditorController {

    @FXML
    private TextArea editorArea;

    private boolean saved = false;

    public void setText(String text) {
        editorArea.setText(text);
    }

    public String getText() {
        return editorArea.getText();
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void onSave() {
        saved = true;
        close();
    }

    @FXML
    private void onCancel() {
        saved = false;
        close();
    }

    private void close() {
        Stage stage = (Stage) editorArea.getScene().getWindow();
        stage.close();
    }
}
