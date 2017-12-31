package com.practice.jira;

/**
 * Created by Tomer.Ben-Dror on 2/14/2017
 */
public class SprintTimeRemaining {
    private String text;
    private boolean isFuture;

    public SprintTimeRemaining() {
    }

    public String getText() {
        return text;
    }

    public boolean isFuture() {
        return isFuture;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFuture(boolean future) {
        isFuture = future;
    }
}
