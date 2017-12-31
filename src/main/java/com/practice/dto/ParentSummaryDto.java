package com.practice.dto;

import com.practice.model.ParentUser;

/**
 * Created by Tomer
 */
public class ParentSummaryDto {
    private Long parentId;
    private Integer childCount;
    private Integer groupsCount;
    private Integer practicesCount;
    private String displayName;
    private String profileImageUrl;

    public ParentSummaryDto(ParentUser parent, int practicesCount, String profileImageUrl) {
        this.parentId = parent.getId();
        this.childCount =  parent.getChilds().size();
        this.groupsCount = parent.getParentToGroups().size();
        this.displayName = parent.getDisplayName();
        this.practicesCount = practicesCount;
        this.profileImageUrl = profileImageUrl;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public Integer getGroupsCount() {
        return groupsCount;
    }

    public void setGroupsCount(Integer groupsCount) {
        this.groupsCount = groupsCount;
    }

    public Integer getPracticesCount() {
        return practicesCount;
    }

    public void setPracticesCount(Integer practicesCount) {
        this.practicesCount = practicesCount;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
