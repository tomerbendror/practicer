package com.practice.model;

import com.practice.security.PracticerAuthority;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

/**
 * User: tomer
 */
@Entity
@Table(name="admin_user")
public class AdminUser extends User {

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = super.getAuthorities();
        authorities.add(PracticerAuthority.ADMIN.role);
        return authorities;
    }
}
