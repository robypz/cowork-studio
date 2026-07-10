package com.orinocolabs.cowork_studio.identity.domain.exception;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Email;
import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

/**
 * Raised when an email uniqueness check fails. Uniqueness spans every
 * {@code User} in the repository, so this is enforced by the application
 * layer (via {@code UserRepository#existsByEmail}) rather than inside the
 * {@code User} aggregate itself — the aggregate has no way to see other
 * aggregates.
 */
public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException(Email email) {
        super("email '" + email + "' is already registered");
    }
}
