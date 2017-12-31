package com.practice.dto.group;

/**
 * User: tomer
 */
public class GroupInvitesDto {
    private ParentsGroupDto updatedGroup;
    private int newInvites;
    private int resendInvites;

    public GroupInvitesDto(ParentsGroupDto updatedGroup, int newInvites, int resendInvites) {
        this.updatedGroup = updatedGroup;
        this.newInvites = newInvites;
        this.resendInvites = resendInvites;
    }

    public GroupInvitesDto() {
    }

    public ParentsGroupDto getUpdatedGroup() {
        return updatedGroup;
    }

    public void setUpdatedGroup(ParentsGroupDto updatedGroup) {
        this.updatedGroup = updatedGroup;
    }

    public int getNewInvites() {
        return newInvites;
    }

    public void setNewInvites(int newInvites) {
        this.newInvites = newInvites;
    }

    public int getResendInvites() {
        return resendInvites;
    }

    public void setResendInvites(int resendInvites) {
        this.resendInvites = resendInvites;
    }
}
