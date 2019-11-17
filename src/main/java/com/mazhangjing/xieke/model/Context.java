package com.mazhangjing.xieke.model;

import com.mazhangjing.xieke.model.api.Condition;
import com.mazhangjing.xieke.model.api.Emotion;

import java.util.List;
import java.util.Map;

public class Context {
    Question question;
    Condition condition;
    public List<Map<String,String>> emotionChooses;
    //以下均需要被试回答
    Emotion emotion;
    Integer answer;
    Integer useTime;

    public static Context of(Question question) {
        Context c = new Context();
        c.question = question;
        return c;
    }

    public Context at(Condition condition) {
        this.condition = condition;
        return this;
    }

    public Context willShow(List<Map<String,String>> emotions) {
        this.emotionChooses = emotions;
        return this;
    }


    public List<Map<String, String>> getEmotionChooses() {
        return emotionChooses;
    }

    public void setEmotionChooses(List<Map<String, String>> emotionChooses) {
        this.emotionChooses = emotionChooses;
    }

    @Override
    public String toString() {
        return "Context{" +
                "question=" + question +
                ", condition=" + condition +
                ", emotion=" + emotion +
                ", answer=" + answer +
                ", useTime=" + useTime +
                '}';
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }

    public Integer getUseTime() {
        return useTime;
    }

    public void setUseTime(Integer useTime) {
        this.useTime = useTime;
    }

    public Context(Question question, Condition condition, Emotion emotion, Integer answer, Integer useTime) {
        this.question = question;
        this.condition = condition;
        this.emotion = emotion;
        this.answer = answer;
        this.useTime = useTime;
    }

    public Context() {
    }
}
