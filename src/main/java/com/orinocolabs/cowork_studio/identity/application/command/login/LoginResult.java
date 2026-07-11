package com.orinocolabs.cowork_studio.identity.application.command.login;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/** Output of a successful login. */
public record LoginResult(UserId userId, Role role, String accessToken) {
}
