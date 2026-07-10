package com.orinocolabs.cowork_studio.identity.domain;

import com.orinocolabs.cowork_studio.shared.domain.DomainException;

public class UserAlreadyDeactivatedException extends DomainException {

    public UserAlreadyDeactivatedException(UserId id) {
        super("user " + id + " is already deactivated");
    }
}
