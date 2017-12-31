package com.practice.jira;

import com.practice.jira.full.AllDataJson;

/**
 * Created by Tomer
 */
public class JiraJsonWrapper {
    private JiraJsonObj jiraJsonObj;
    private AllDataJson allDataJson;

    public JiraJsonWrapper() {
    }

    public JiraJsonObj getJiraJsonObj() {
        return jiraJsonObj;
    }

    public void setJiraJsonObj(JiraJsonObj jiraJsonObj) {
        this.jiraJsonObj = jiraJsonObj;
    }

    public AllDataJson getAllDataJson() {
        return allDataJson;
    }

    public void setAllDataJson(AllDataJson allDataJson) {
        this.allDataJson = allDataJson;
    }
}
