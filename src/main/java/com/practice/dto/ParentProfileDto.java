package com.practice.dto;

import com.practice.model.ParentUser;
import com.practice.property.UserProperties;

/**
 * Created by Tomer
 */
public class ParentProfileDto {
    private Long parentId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String profileImageUrl;
    private Integer childCount;

    private boolean receiveDailyMail;
    private int dailyMailLocalHour;

    public ParentProfileDto() {
    }

    public ParentProfileDto(ParentUser parent) {
        this.parentId = parent.getId();
        this.firstName = parent.getFirstName();
        this.lastName = parent.getLastName();
        this.displayName = parent.getDisplayName();
        this.email = parent.getEmail();
        this.profileImageUrl = parent.getProfileImageUrlOrDefault();
        this.childCount = parent.getChilds().size();

        this.receiveDailyMail = parent.getPropertyValue(UserProperties.RECEIVE_DAILY_MAIL_KEY);
        this.dailyMailLocalHour = parent.getPropertyValue(UserProperties.DAILY_MAIL_HOUR_KEY);
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isReceiveDailyMail() {
        return receiveDailyMail;
    }

    public void setReceiveDailyMail(boolean receiveDailyMail) {
        this.receiveDailyMail = receiveDailyMail;
    }

    public int getDailyMailLocalHour() {
        return dailyMailLocalHour;
    }

    public void setDailyMailLocalHour(int dailyMailLocalHour) {
        this.dailyMailLocalHour = dailyMailLocalHour;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }
}
