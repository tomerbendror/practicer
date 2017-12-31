package com.practice.model;

import com.practice.security.PracticerAuthority;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: tomer
 */
@Entity
@Table(name="parent_user")
public class ParentUser extends User {

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="parent_to_child",
            joinColumns={@JoinColumn(name="PARENT_ID")},
            inverseJoinColumns={@JoinColumn(name="CHILD_ID")})
    private List<ChildUser> childs = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParentToGroup> parentToGroups = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade = CascadeType.ALL)
    private Set<Practice> createdPractices = new HashSet<>();

    public List<ChildUser> getChilds() {
        return childs;
    }

    public void setChilds(List<ChildUser> childs) {
        this.childs = childs;
    }

    public Set<ParentToGroup> getParentToGroups() {
        return parentToGroups;
    }

    public void setParentToGroups(Set<ParentToGroup> usersToGroups) {
        this.parentToGroups = usersToGroups;
    }

    public Set<Practice> getCreatedPractices() {
        return createdPractices;
    }

    public void setCreatedPractices(Set<Practice> createdPractices) {
        this.createdPractices = createdPractices;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = super.getAuthorities();
        authorities.add(PracticerAuthority.PARENT.role);
        return authorities;
    }

    public String getProfileImageUrlOrDefault() {
        return StringUtils.isNotBlank(getProfileImageUrl()) ? getProfileImageUrl() : "/wrapkit/images/dummy/profile.jpg";
    }
}
