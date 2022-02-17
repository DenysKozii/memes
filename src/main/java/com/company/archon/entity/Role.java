package com.company.archon.entity;
import org.springframework.security.core.GrantedAuthority;

/**
 * Date: 07.09.2020
 *
 * @author Denys Kozii
 */
public enum Role implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}