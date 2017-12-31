package com.practice.jira.enums;

/**
 * Created by Tomer.Ben-Dror on 2/12/2017
 */
public enum StatusName {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    REOPENED("Reopened"),
    CLOSED("Closed");

    private String strValue;

    StatusName(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return strValue;
    }

    @Override
    public String toString() {
        return getStrValue();
    }
}
