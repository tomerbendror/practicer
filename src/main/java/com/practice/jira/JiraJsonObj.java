package com.practice.jira;

import java.util.List;

/**
 * Created by Tomer.Ben-Dror on 2/12/2017
 */
public class JiraJsonObj {
    private List<JiraIssue> issues;
    private List<JiraSprint> sprints;

    public JiraJsonObj() {
    }

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }

    public List<JiraSprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<JiraSprint> sprints) {
        this.sprints = sprints;
    }
}
