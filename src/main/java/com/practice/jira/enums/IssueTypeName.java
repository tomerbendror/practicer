package com.practice.jira.enums;

/**
 * Created by Tomer.Ben-Dror on 2/12/2017
 */
public enum IssueTypeName {
    STORY("Story"),
    SUB_TASK("Sub-task"),
    QA_STORY("QA Story"),
    QA_TASK("QA Task"),
    BUG("Bug"),
    DEV_OPS_STORY("OPs Story"),
    DEV_OPS_TASK("OPs Task"),
    TASK("Task"),
    UST_BUG("UST Bug"),
    IMPROVEMENT("Improvement");

    private String strValue;

    IssueTypeName(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return strValue;
    }
}


