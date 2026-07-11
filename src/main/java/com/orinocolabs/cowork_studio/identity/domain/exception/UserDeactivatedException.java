package com.orinocolabs.cowork_studio.identity.domain.exception;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

/**
 * Raised when login credentials are correct but the account has been
 * deactivated. Only thrown after the password has already been verified, so
 * revealing this (unlike {@link InvalidCredentialsException}) is not a user
 * enumeration risk — the caller has already proven they own the account.
 */
public class UserDeactivatedException extends DomainException {

    public UserDeactivatedException(UserId id) {
        super("user " + id + " is deactivated");
    }
}
