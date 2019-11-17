package com.mazhangjing.xieke.model;

import java.util.List;

public class Question {
    String kind;
    String explain;
    String body;
    List<String> choose;
    Integer rightChoose;

    @Override
    public String toString() {
        return "Question{" +
                "kind='" + kind + '\'' +
                ", explain='" + explain + '\'' +
                ", body='" + body + '\'' +
                ", choose=" + choose +
                ", rightChoose=" + rightChoose +
                '}';
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getChoose() {
        return choose;
    }

    public void setChoose(List<String> choose) {
        this.choose = choose;
    }

    public Integer getRightChoose() {
        return rightChoose;
    }

    public void setRightChoose(Integer rightChoose) {
        this.rightChoose = rightChoose;
    }
}
