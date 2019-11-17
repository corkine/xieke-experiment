package com.mazhangjing.xieke.model;

import com.mazhangjing.xieke.model.api.AskCount;

import java.util.List;

/**
 * 问卷的抽象，包括 Question 问题，以及问卷的名称、答案个数、问卷简介和被试回答
 */
public class Survey {
    String name;
    AskCount kind;
    String intro;
    List<Question> questions;
    List<Integer> choices;

    public Survey copy() {
        Survey cop = new Survey();
        cop.setName(name);
        cop.setKind(kind);
        cop.setIntro(intro);
        cop.setQuestions(questions);
        cop.setChoices(null);
        return cop;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "intro='" + intro + '\'' +
                ", kind=" + kind +
                ", name='" + name + '\'' +
                ", questions=" + questions +
                ", choices=" + choices +
                '}';
    }

    public AskCount getKind() {
        return kind;
    }

    public void setKind(AskCount kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Integer> getChoices() {
        return choices;
    }

    public void setChoices(List<Integer> choices) {
        this.choices = choices;
    }

    public Survey(AskCount kind, String name, List<Question> questions, List<Integer> choices) {
        this.kind = kind;
        this.name = name;
        this.questions = questions;
        this.choices = choices;
    }

    public Survey() {
    }
}
