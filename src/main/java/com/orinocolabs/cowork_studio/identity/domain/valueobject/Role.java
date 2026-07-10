package com.orinocolabs.cowork_studio.identity.domain.valueobject;

import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidRoleException;

/**
 * Roles recognized by the identity bounded context for this MVP.
 * ADMIN manages spaces and can see every booking; CLIENT books spaces for
 * themselves.
 */
public enum Role {
    ADMIN,
    CLIENT;

    public static Role fromString(String rawValue) {
        try {
            return Role.valueOf(rawValue == null ? "" : rawValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException(rawValue);
        }
    }
}
