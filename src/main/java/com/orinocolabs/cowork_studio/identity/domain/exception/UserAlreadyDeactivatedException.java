package com.orinocolabs.cowork_studio.identity.domain.exception;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

public class UserAlreadyDeactivatedException extends DomainException {

    public UserAlreadyDeactivatedException(UserId id) {
        super("user " + id + " is already deactivated");
    }
}
