package com.mazhangjing.xieke.model;

import java.util.List;
import java.util.Map;

public class Config {
    List<Map<String,String>> emotionChooses;
    List<Question> questions;
    List<Survey> surveys;

    @Override
    public String toString() {
        return "Config{" +
                "emotionChooses=" + emotionChooses +
                ", questions=" + questions +
                ", surveys=" + surveys +
                '}';
    }

    public List<Map<String, String>> getEmotionChooses() {
        return emotionChooses;
    }

    public void setEmotionChooses(List<Map<String, String>> emotionChooses) {
        this.emotionChooses = emotionChooses;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    public Config() {
    }
}
