package com.practice.jira.full;

/**
 * Created by Tomer.Ben-Dror on 2/13/2017
 */
public class AllDataJson {
    private IssuesData issuesData;

    public AllDataJson(IssuesData issuesData) {
        this.issuesData = issuesData;
    }

    public IssuesData getIssuesData() {
        return issuesData;
    }

    public void setIssuesData(IssuesData issuesData) {
        this.issuesData = issuesData;
    }
}
