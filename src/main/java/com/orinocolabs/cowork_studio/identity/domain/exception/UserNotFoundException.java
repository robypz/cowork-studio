package com.orinocolabs.cowork_studio.identity.domain.exception;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

/**
 * Raised when a use case needs to load a user by id (e.g. to read or update
 * a profile) and no such user exists. Under normal operation this should not
 * happen for an authenticated caller — the id comes from a valid JWT subject
 * — but the account could have been deleted after the token was issued.
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UserId id) {
        super("user " + id + " not found");
    }
}
