<html>
<head>
    <title>Sprint ${sprintNum} Report</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

    <script
            src="https://code.jquery.com/jquery-2.2.4.min.js"
            integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44="
            crossorigin="anonymous"></script>

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
            crossorigin="anonymous"></script>

    <style>
        .label-as-badge {
            border-radius: 1em;
        }

        .table-danger, .table-danger>td, .table-danger>th {
            background-color: #f2dede;
        }
    </style>

</head>
<body style="font-family: Arial,sans-serif;line-height: 2;">

<div style="position: fixed;z-index: 1032;padding: 5px">
<#if isFutureSprint == true>
    <div class="alert alert-success" style="margin-left: 5px;display: inline-block;font-size: 20px;padding: 9px 30px;"> <strong>Future Sprint!</strong></div>
<#else>
    <div class="alert alert-warning" style="margin-left: 5px;display: inline-block;font-size: 20px;padding: 9px 30px;"> <strong>Active Sprint!</strong> ${timeRemaining} </div>
</#if>

</div>
<nav class="navbar navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header" style="width: 100%;">
            <a href="${sprintUrl}" target="_blank"><h1 class="navbar-brand" style="width: 100%;text-align: center;font-size: 30px;margin-top: 10px;">Sprint ${sprintNum} Report</h1></a>
        </div>
    </div>
</nav>
<div style="padding-bottom: 100px"></div>

<!-- Nav tabs -->
<ul class="nav nav-tabs" role="tablist">
    <li role="presentation"><a href="#stories" aria-controls="home" role="tab" data-toggle="tab">Stories by Person</a></li>
    <li role="presentation" class="active"><a href="#sub-tasks-by-person" aria-controls="profile" role="tab" data-toggle="tab">Sub-Tasks by Person</a></li>
    <li role="presentation"><a href="#board-by-priority" aria-controls="profile" role="tab" data-toggle="tab">Board by Priority</a></li>
</ul>

<!-- Tab panes -->
<div class="tab-content">
    <div role="tabpanel" class="tab-pane fade" id="stories">
        <div style="padding: 0 20px">

        <#list storySummaries as storiesSummary>
            <strong style="padding-left: 10px; font-size: 24px; color: darkcyan;">${storiesSummary.user}</strong>
            <table class="table table-bordered">
                <col width="190px">
                <col>
                <col width="110px">
                <col width="100px">
                <tr>
                    <th style="text-align: center;padding: 4px;">Story</th>
                    <th style="padding: 4px;">Description</th>
                    <th style="text-align: center;padding: 4px;">Status</th>
                    <th style="text-align: center;padding: 4px;">Time</th>
                </tr>

                <#list storiesSummary.stories as story>
                    <tr>
                        <td style="text-align: center;padding: 4px;"><a href="${story.browseUrl()}" target="_blank" style="text-decoration: none;">${story.key}</a>
                            <#if story.getIssueType() == "STORY" >
                                <span class="label label-info">${story.getIssueType().getStrValue()}</span>
                            <#elseif story.getIssueType() == "QA_STORY">
                                <span class="label label-danger">${story.getIssueType().getStrValue()}</span>
                            <#elseif story.getIssueType() == "BUG">
                                <span class="label label-danger">${story.getIssueType().getStrValue()}</span>
                            <#elseif story.getIssueType() == "DEV_OPS_STORY">
                                <span class="label label-warning">${story.getIssueType().getStrValue()}</span>
                            <#else>
                                <span class="label label-success">${story.getIssueType().getStrValue()}</span>
                            </#if>
                        </td>
                        <td style="padding: 4px;">${story.summary}</td>
                        <td style="text-align: center;">
                            <#if story.statusName == "Closed">
                                <span class="label label-success">${story.statusName}</span>
                            <#elseif story.statusName == "In Progress">
                                <span class="label label-primary">${story.statusName}</span>
                            <#else>
                                <span class="label label-warning">${story.statusName}</span>
                            </#if>
                        </td>
                        <td style="text-align: center;"><span class="label label-default label-as-badge">${story.getTotalTimeStr()}</span></td>
                    </tr>
                </#list>
                <tr style="height: 40px;">
                    <td colspan="3"><span style="padding: 15px;font-weight: bold;">Total open stories time</span></td>
                    <td style="text-align: center;padding-top: 11px;">
                        <#if storiesSummary.getTotalTimeStr() == "">   <#-- value is 0 -->
                            <span style="font-size: 15px;" class="label label-danger label-as-badge">0</span>
                        <#else>
                            <span style="font-size: 15px;" class="label label-primary label-as-badge">${storiesSummary.getTotalTimeStr()}</span>
                        </#if>
                    </td>
                </tr>
            </table>
            <br>
        </#list>
        </div>
    </div>
    <div role="tabpanel" class="tab-pane fade in active" id="sub-tasks-by-person">
        <div style="padding: 0 20px">
        <#list personTasksSummaries as personSummary>
            <strong style="padding-left: 10px; font-size: 24px; color: darkcyan;">${personSummary.assigneeName}</strong>

            <table class="table table-bordered">
                <col width="190px">
                <col>
                <col width="170px">
                <col width="110px">
                <col width="100px">
                <tr>
                    <th style="text-align: center;padding: 4px;">Issue</th>
                    <th style="padding: 4px;">Description</th>
                    <th style="text-align: center;padding: 4px;">Assignee</th>
                    <th style="text-align: center;padding: 4px;">Status</th>
                    <th style="text-align: center;padding: 4px;">Time</th>
                </tr>

                <#list personSummary.personRelevantStories as personTask>
                    <#if personTask.isNoEstimation() >
                    <tr class="table-danger">
                    <#else>
                    <tr>
                    </#if>

                    <td style="text-align: center;padding: 4px;">
                        <a href="${personTask.issue.browseUrl()}" target="_blank" style="text-decoration: none;">${personTask.issue.key}</a>

                        <#if personTask.issue.getIssueType() == "STORY" >
                            <span class="label label-info">${personTask.issue.getIssueType().getStrValue()}</span>
                        <#elseif personTask.issue.getIssueType() == "QA_STORY">
                            <span class="label label-danger">${personTask.issue.getIssueType().getStrValue()}</span>
                        <#elseif personTask.issue.getIssueType() == "BUG">
                            <span class="label label-danger">${personTask.issue.getIssueType().getStrValue()}</span>
                        <#elseif personTask.issue.getIssueType() == "DEV_OPS_STORY">
                            <span class="label label-warning">${personTask.issue.getIssueType().getStrValue()}</span>
                        <#else>
                            <span class="label label-success">${personTask.issue.getIssueType().getStrValue()}</span>
                        </#if>

                    </td>
                    <td style="padding: 4px;">${personTask.getIssue().summary}
                        <#if personTask.isNoEstimation() >
                            <span class="label label-danger">No Estimation</span>
                        </#if>
                    </td>
                    <td style="padding: 4px;">${personTask.getIssue().assigneeName}</td>
                    <td style="text-align: center;">
                        <#if personTask.getIssue().statusName == "Closed">
                            <span class="label label-success">${personTask.getIssue().statusName}</span>
                        <#elseif personTask.getIssue().statusName == "Resolved">
                            <span class="label label-success">${personTask.getIssue().statusName}</span>
                        <#elseif personTask.getIssue().statusName == "In Progress">
                            <span class="label label-primary">${personTask.getIssue().statusName}</span>
                        <#else>
                            <span class="label label-warning">${personTask.getIssue().statusName}</span>
                        </#if>
                    </td>

                    <td style="text-align: center;">
                        <#if personTask.countToWork == true>
                            <span class="label label-primary label-as-badge">${personTask.getIssue().getTotalTimeStr()}</span>
                        <#else>
                            <span class="label label-default label-as-badge">${personTask.getIssue().getTotalTimeStr()}</span>
                        </#if>
                    </td>
                </tr>


                <#-- subtasks -->
                    <#list personTask.personSubtasks as subTask>
                        <#if subTask.isNoEstimation() >
                        <tr class="table-danger">
                        <#else>
                        <tr>
                        </#if>
                        <td style="text-align: center;padding: 4px;"><a href="${subTask.issue.browseUrl()}" target="_blank" style="text-decoration: none;">${subTask.issue.key}</a></td>
                        <td style="padding: 4px;">
                            <#if subTask.issue.typeName == "SUB_TASK">
                                <span class="label label-primary">${subTask.issue.typeName.getStrValue()}</span>
                            <#elseif subTask.issue.typeName == "QA_TASK">
                                <span class="label label-danger">${subTask.issue.typeName.getStrValue()}</span>
                            <#else>
                                <span class="label label-warning">${subTask.issue.typeName.getStrValue()}</span>
                            </#if>
                        ${subTask.issue.summary}
                            <#if subTask.isNoEstimation() >
                                <span class="label label-danger">No Estimation</span>
                            </#if>
                        </td>
                        <td style="padding: 4px;">${subTask.issue.assigneeName}</td>
                        <td style="text-align: center;">
                            <#if subTask.issue.statusName == "Closed">
                                <span class="label label-success">${subTask.issue.statusName}</span>
                            <#elseif subTask.issue.statusName == "Resolved">
                                <span class="label label-success">${subTask.issue.statusName}</span>
                            <#elseif subTask.issue.statusName == "In Progress">
                                <span class="label label-primary">${subTask.issue.statusName}</span>
                            <#else>
                                <span class="label label-warning">${subTask.issue.statusName}</span>
                            </#if>
                        </td>

                        <td style="text-align: center;">
                            <#if subTask.countToWork == true>
                                <span class="label label-primary label-as-badge">${subTask.issue.getTotalTimeStr()}</span>
                            <#else>
                                <span class="label label-default label-as-badge">${subTask.issue.getTotalTimeStr()}</span>
                            </#if>
                        </td>
                    </tr>
                    </#list>
                </#list>

                <tr style="height: 40px;">
                    <td colspan="4"><span style="padding: 15px;font-weight: bold;">Total remain tasks time</span></td>
                    <td style="text-align: center;padding-top: 11px;">
                        <#if personSummary.getTotalTimeStr() == "">   <#-- value is 0 -->
                            <span style="font-size: 15px;" class="label label-danger label-as-badge">0</span>
                        <#elseif (personSummary.getTotalWorkInSeconds() > totalWorkInSeconds) >
                            <span style="font-size: 15px;" class="label label-danger label-as-badge">${personSummary.getTotalTimeStr()}</span>
                        <#else>
                            <span style="font-size: 15px;" class="label label-success label-as-badge">${personSummary.getTotalTimeStr()}</span>
                        </#if>
                    </td>
                </tr>
            </table>
        </#list>
        </div>
    </div>
    <div role="tabpanel" class="tab-pane fade" id="board-by-priority">
        <div style="padding: 0 20px">
            <table class="table table-bordered">
                <col width="190px">
                <col>
                <col>
                <col width="110px">
                <col width="100px">
                <tr>
                    <th style="text-align: center;padding: 4px;">Issue</th>
                    <th style="padding: 4px;">Description</th>
                    <th style="text-align: center;padding: 4px;">Assignee</th>
                    <th style="text-align: center;padding: 4px;">Status</th>
                    <th style="text-align: center;padding: 4px;">Time</th>
                </tr>

            <#list storiesList as story>

                <tr>
                    <td style="text-align: center;padding: 4px;">
                        <a href="${story.browseUrl()}" target="_blank" style="text-decoration: none;">${story.key}</a>
                        <#if story.getIssueType() == "STORY" >
                            <span class="label label-info">${story.getIssueType().getStrValue()}</span>
                        <#elseif story.getIssueType() == "QA_STORY">
                            <span class="label label-danger">${story.getIssueType().getStrValue()}</span>
                        <#elseif story.getIssueType() == "BUG">
                            <span class="label label-danger">${story.getIssueType().getStrValue()}</span>
                        <#elseif story.getIssueType() == "DEV_OPS_STORY">
                            <span class="label label-warning">${story.getIssueType().getStrValue()}</span>
                        <#else>
                            <span class="label label-success">${story.getIssueType().getStrValue()}</span>
                        </#if>
                    </td>
                    <td style="padding: 4px;">${story.summary}</td>
                    <td style="padding: 4px;">${story.assigneeName}</td>
                    <td style="text-align: center;">
                        <#if story.statusName == "Closed">
                            <span class="label label-success">${story.statusName}</span>
                        <#elseif story.statusName == "Resolved">
                            <span class="label label-success">${story.statusName}</span>
                        <#elseif story.statusName == "In Progress">
                            <span class="label label-primary">${story.statusName}</span>
                        <#else>
                            <span class="label label-warning">${story.statusName}</span>
                        </#if>
                    </td>
                    <td style="text-align: center;"><span class="label label-primary label-as-  badge">${story.getTotalTimeStr()}</span></td>
                </tr>

                <#list story.subtasks as subTask>
                    <tr>
                        <td style="text-align: center;padding: 4px;"><a href="${subTask.browseUrl()}" target="_blank" style="text-decoration: none;">${subTask.key}</a></td>
                        <td style="padding: 4px;">
                            <#if subTask.typeName == "SUB_TASK">
                                <span class="label label-primary">${subTask.typeName.getStrValue()}</span>
                            <#elseif subTask.typeName == "QA_TASK">
                                <span class="label label-danger">${subTask.typeName.getStrValue()}</span>
                            <#else>
                                <span class="label label-warning">${subTask.typeName.getStrValue()}</span>
                            </#if>
                        ${subTask.summary}
                        </td>
                        <td style="padding: 4px;">${subTask.assigneeName}</td>
                        <td style="text-align: center;">
                            <#if subTask.statusName == "Closed">
                                <span class="label label-success">${subTask.statusName}</span>
                            <#elseif subTask.statusName == "Resolved">
                                <span class="label label-success">${subTask.statusName}</span>
                            <#elseif subTask.statusName == "In Progress">
                                <span class="label label-primary">${subTask.statusName}</span>
                            <#else>
                                <span class="label label-warning">${subTask.statusName}</span>
                            </#if>
                        </td>
                        <td style="text-align: center;"><span class="label label-primary label-as-badge">${subTask.getTotalTimeStr()}</span></td>
                    </tr>
                </#list>
            </#list>
            </table>
        </div>
    </div>
</div>

</body>
</html>