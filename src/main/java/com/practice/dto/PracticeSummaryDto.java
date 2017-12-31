package com.practice.dto;

import com.practice.model.ParentUser;
import com.practice.model.ParentsGroup;
import com.practice.model.Practice;
import com.practice.model.QuestionType;

import java.sql.Timestamp;

/**
 * Created by Tomer
 */
@SuppressWarnings("unused")
public class PracticeSummaryDto {
    private Long id;
    private String name;
    private String description;
    private QuestionType questionsType;
    private Boolean useTTS;
    private Integer questionCount;

    private Long creatorId;
    private String creatorFullName;

    private Long groupId;
    private String groupName;
    private String groupDescription;
    private Timestamp createdTime;

    public PracticeSummaryDto() {
    }

    public PracticeSummaryDto(Practice practice) {
        this(practice, null, null);
    }

    public PracticeSummaryDto(Practice practice, ParentsGroup group) {
        this(practice, null, group);
    }

    public PracticeSummaryDto(Practice practice, ParentUser parent) {
        this(practice, parent, null);
    }

    public PracticeSummaryDto(Practice practice, ParentUser parent, ParentsGroup group) {
        id = practice.getId();
        name = practice.getName();
        description = practice.getDescription();
        questionsType = practice.getQuestionType();
        useTTS = practice.getUseTTS();
        questionCount = practice.getQuestions().size();
        createdTime = practice.getCreatedTime();

        ParentUser creator = practice.getCreator();
        if (creator != null) {  // user practice and not a system one
            creatorId = creator.getId();
            creatorFullName = creator.getFullName();
        }

        if (group != null) {
            groupId = group.getId();
            groupName = group.getName();
            groupDescription = group.getDescription();
        } else {
            groupId = -1L;
        }
    }

    public Long getId() {
        return id;
    }

    public void setPracticeId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestionType getQuestionsType() {
        return questionsType;
    }

    public void setQuestionType(QuestionType questionsType) {
        this.questionsType = questionsType;
    }

    public Boolean getUseTTS() {
        return useTTS;
    }

    public void setUseTTS(Boolean useTTS) {
        this.useTTS = useTTS;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorFullName() {
        return creatorFullName;
    }

    public void setCreatorFullName(String creatorFullName) {
        this.creatorFullName = creatorFullName;
    }

    public void setQuestionsType(QuestionType questionsType) {
        this.questionsType = questionsType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public long getCreatedTimeMillis() {
        return createdTime.getTime();
    }
}
