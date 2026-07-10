package com.orinocolabs.cowork_studio.identity.domain;

import com.orinocolabs.cowork_studio.shared.domain.DomainException;

public class InvalidEmailException extends DomainException {

    public InvalidEmailException(String rawValue) {
        super("'" + rawValue + "' is not a valid email address");
    }
}
