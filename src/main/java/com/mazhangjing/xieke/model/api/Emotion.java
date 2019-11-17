package com.mazhangjing.xieke.model.api;

/**
 * 表情的种类
 */
public class Emotion {
    String kind;

    public static Emotion of(String kind) {
        Emotion e = new Emotion();
        e.kind = kind;
        return e;
    }

    @Override
    public String toString() {
        return "Emotion{" +
                "kind='" + kind + '\'' +
                '}';
    }

    public Emotion() {
    }

    public Emotion(String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
