package com.practice.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * User: tomer
 */
@Entity
@Table(name="child_to_group")
public class ChildToGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHILD_ID", nullable = false)
    private ChildUser childUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private ParentsGroup parentsGroup;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "JOIN_DATE")
    private Date joinDate;

    public ChildToGroup() {
    }

    public ChildToGroup(ChildUser childUser, ParentsGroup parentsGroup) {
        this.childUser = childUser;
        this.parentsGroup = parentsGroup;
        joinDate = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildToGroup that = (ChildToGroup) o;
        return Objects.equals(getChildUser(), that.getChildUser()) &&
                Objects.equals(getParentsGroup(), that.getParentsGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getChildUser(), getParentsGroup());
    }

    public ChildUser getChildUser() {
        return childUser;
    }

    public void setParentUser(ChildUser childUser) {
        this.childUser = childUser;
    }

    public ParentsGroup getParentsGroup() {
        return parentsGroup;
    }

    public void setParentsGroup(ParentsGroup parentsGroup) {
        this.parentsGroup = parentsGroup;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}
