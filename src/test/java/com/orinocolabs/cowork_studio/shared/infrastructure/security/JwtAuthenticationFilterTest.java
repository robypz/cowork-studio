package com.orinocolabs.cowork_studio.shared.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.orinocolabs.cowork_studio.identity.domain.valueobject.Role;
import com.orinocolabs.cowork_studio.identity.domain.valueobject.UserId;
import com.orinocolabs.cowork_studio.identity.infrastructure.out.security.JwtTokenIssuerAdapter;

/**
 * Exercises the filter against real jjwt-issued tokens (via
 * {@link JwtTokenIssuerAdapter}, same as production) rather than a fake, to
 * prove the round trip: a token minted at login is accepted here and its
 * subject/role land in the security context the way controllers expect.
 */
class JwtAuthenticationFilterTest {

    private static final String SECRET = "test-only-secret-key-please-32-bytes-min";

    private final JwtProperties properties = new JwtProperties(SECRET, 60);
    private final JwtTokenIssuerAdapter tokenIssuer = new JwtTokenIssuerAdapter(properties);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(properties);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticates_the_caller_from_a_valid_bearer_token() throws Exception {
        UserId userId = UserId.newId();
        String token = tokenIssuer.issueAccessToken(userId, Role.ADMIN);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userId.value().toString(), authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void leaves_the_request_unauthenticated_when_there_is_no_token() throws Exception {
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void leaves_the_request_unauthenticated_when_the_token_is_invalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer not-a-real-token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
