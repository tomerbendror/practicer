package com.practice.model;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.Date;

/**
 * User: tomer
 */
@Entity
@Table(name="group_invitation")
public class GroupInvitation extends BaseEntity {

    @Column(name = "EMAIL")
    @Email(message = "יש להזין כתובת אימייל חוקית")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private ParentsGroup parentsGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITER_ID", nullable = false)
    private ParentUser inviter;

    @Column(name = "INVITE_DATE")
    private Date inviteDate;

    @Column(name = "INVITATION_KEY")
    private String invitationKey;

    @Column(name = "WAS_USED")
    private boolean wasUsed;

    public GroupInvitation() {
    }

    public ParentsGroup getParentsGroup() {
        return parentsGroup;
    }

    public void setParentsGroup(ParentsGroup parentsGroup) {
        this.parentsGroup = parentsGroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ParentUser getInviter() {
        return inviter;
    }

    public void setInviter(ParentUser inviter) {
        this.inviter = inviter;
    }

    public Date getInviteDate() {
        return inviteDate;
    }

    public void setInviteDate(Date inviteDate) {
        this.inviteDate = inviteDate;
    }

    public boolean isWasUsed() {
        return wasUsed;
    }

    public void setWasUsed(boolean wasUsed) {
        this.wasUsed = wasUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupInvitation)) return false;

        GroupInvitation that = (GroupInvitation) o;

        if (!email.equals(that.email)) return false;
        if (parentsGroup != null ? !parentsGroup.equals(that.parentsGroup) : that.parentsGroup != null) return false;
        if (inviter != null ? !inviter.equals(that.inviter) : that.inviter != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + (parentsGroup != null ? parentsGroup.hashCode() : 0);
        result = 31 * result + (inviter != null ? inviter.hashCode() : 0);
        return result;
    }

    public String getInvitationKey() {
        return invitationKey;
    }

    public void setInvitationKey(String key) {
        this.invitationKey = key;
    }
}
