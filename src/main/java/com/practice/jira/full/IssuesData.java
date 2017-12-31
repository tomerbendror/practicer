package com.practice.jira.full;


import com.practice.jira.JiraIssue;

import java.util.List;

/**
 * Created by Tomer.Ben-Dror on 2/13/2017
 */
public class IssuesData {
    private List<JiraIssue> issues;

    public IssuesData(List<JiraIssue> issues) {
        this.issues = issues;
    }

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }
}
