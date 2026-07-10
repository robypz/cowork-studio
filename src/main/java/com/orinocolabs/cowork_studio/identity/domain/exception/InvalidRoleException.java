package com.orinocolabs.cowork_studio.identity.domain.exception;

import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

public class InvalidRoleException extends DomainException {

    public InvalidRoleException(String rawValue) {
        super("'" + rawValue + "' is not a valid role");
    }
}
