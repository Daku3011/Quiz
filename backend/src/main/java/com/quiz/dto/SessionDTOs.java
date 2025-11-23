package com.quiz.dto;

import java.util.List;
import java.util.Map;

public class SessionDTOs {

    public static class StartSessionRequest {
        private String title;
        private List<QuestionRequest> questions;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public List<QuestionRequest> getQuestions() { return questions; }
        public void setQuestions(List<QuestionRequest> questions) { this.questions = questions; }
    }

    public static class QuestionRequest {
        private String text;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correct;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getOptionA() { return optionA; }
        public void setOptionA(String optionA) { this.optionA = optionA; }
        public String getOptionB() { return optionB; }
        public void setOptionB(String optionB) { this.optionB = optionB; }
        public String getOptionC() { return optionC; }
        public void setOptionC(String optionC) { this.optionC = optionC; }
        public String getOptionD() { return optionD; }
        public void setOptionD(String optionD) { this.optionD = optionD; }
        public String getCorrect() { return correct; }
        public void setCorrect(String correct) { this.correct = correct; }
    }

    public static class JoinSessionRequest {
        private Long sessionId;
        private String otp;

        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }
}
