package com.quiz.dto;

import java.util.List;
import java.util.Map;

public class SessionDTOs {
    // DTOs for Session management

    public static class StartSessionRequest {
        private String title;
        private List<QuestionRequest> questions;
        private String startTime; // ISO-8601
        private Integer durationMinutes;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<QuestionRequest> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionRequest> questions) {
            this.questions = questions;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        private Integer numberOfSets;

        public Integer getNumberOfSets() {
            return numberOfSets;
        }

        public void setNumberOfSets(Integer numberOfSets) {
            this.numberOfSets = numberOfSets;
        }
    }

    public static class QuestionRequest {
        private String text;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correct;
        private String explanation;
        private String chapter;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getOptionA() {
            return optionA;
        }

        public void setOptionA(String optionA) {
            this.optionA = optionA;
        }

        public String getOptionB() {
            return optionB;
        }

        public void setOptionB(String optionB) {
            this.optionB = optionB;
        }

        public String getOptionC() {
            return optionC;
        }

        public void setOptionC(String optionC) {
            this.optionC = optionC;
        }

        public String getOptionD() {
            return optionD;
        }

        public void setOptionD(String optionD) {
            this.optionD = optionD;
        }

        public String getCorrect() {
            return correct;
        }

        public void setCorrect(String correct) {
            this.correct = correct;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getChapter() {
            return chapter;
        }

        public void setChapter(String chapter) {
            this.chapter = chapter;
        }

        private String courseOutcome;

        public String getCourseOutcome() {
            return courseOutcome;
        }

        public void setCourseOutcome(String courseOutcome) {
            this.courseOutcome = courseOutcome;
        }
    }

    public static class JoinSessionRequest {
        private Long sessionId;
        private String otp;

        public Long getSessionId() {
            return sessionId;
        }

        public void setSessionId(Long sessionId) {
            this.sessionId = sessionId;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
}
