package com.orinocolabs.cowork_studio.identity.domain.port;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;

/**
 * Outbound port for issuing an access token after a successful login.
 * Implemented in infrastructure with JWT — the domain only knows it can ask
 * for a token for a given user/role, never the token format or signing
 * details.
 */
public interface TokenIssuer {

    String issueAccessToken(UserId userId, Role role);
}
