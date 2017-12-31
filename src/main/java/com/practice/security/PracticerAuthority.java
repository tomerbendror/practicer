package com.practice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * User: tomer
 */
public enum PracticerAuthority {
    USER("user"),
    CHILD("child"),
    PARENT("parent"),
    ADMIN("admin");

    public final GrantedAuthority role;

    PracticerAuthority(String roleStr) {
        role = new SimpleGrantedAuthority(roleStr);
    }
}
