package com.orinocolabs.cowork_studio.identity.domain;

/**
 * Roles recognized by the identity bounded context for this MVP.
 * ADMIN manages spaces and can see every booking; CLIENT books spaces for
 * themselves.
 */
public enum Role {
    ADMIN,
    CLIENT
}
