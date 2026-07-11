package com.orinocolabs.cowork_studio.identity.domain.exception;

import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

/**
 * Raised for both "no user with that email" and "wrong password". The
 * message is deliberately generic and carries no data (not even the email
 * that was tried) so the web layer can never leak whether a given address
 * is registered.
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("invalid email or password");
    }
}
