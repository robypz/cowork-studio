package com.orinocolabs.cowork_studio.identity.testsupport;

import com.orinocolabs.cowork_studio.identity.domain.port.TokenIssuer;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/** Stand-in for the JWT adapter — returns a predictable, inspectable string. */
public final class FakeTokenIssuer implements TokenIssuer {

    @Override
    public String issueAccessToken(UserId userId, Role role) {
        return "token-for-" + userId.value() + "-" + role.name();
    }
}
