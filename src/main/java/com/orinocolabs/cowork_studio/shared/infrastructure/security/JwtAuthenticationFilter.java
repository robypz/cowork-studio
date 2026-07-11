package com.orinocolabs.cowork_studio.shared.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Validates the {@code Authorization: Bearer <jwt>} header issued by
 * {@code identity}'s {@code JwtTokenIssuerAdapter} and, if valid, populates
 * the security context with the caller's id (subject) and role. Deliberately
 * knows nothing about the {@code identity} domain types ({@code UserId},
 * {@code Role}) — the principal is a plain UUID string and the authority a
 * plain {@code ROLE_*} name — so any bounded context can read
 * {@code SecurityContextHolder} without depending on identity's packages.
 *
 * <p>Missing or invalid tokens are simply left unauthenticated; the request
 * then falls through to {@code SecurityConfig}'s {@code authorizeHttpRequests}
 * rules, which reject it if the endpoint requires authentication.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey signingKey;

    public JwtAuthenticationFilter(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            try {
                var claims = Jwts.parser()
                        .verifyWith(signingKey)
                        .build()
                        .parseSignedClaims(header.substring(BEARER_PREFIX.length()))
                        .getPayload();

                String userId = claims.getSubject();
                String role = claims.get("role", String.class);
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException ignored) {
                // Malformed/expired/invalid-signature token: leave the request
                // unauthenticated rather than failing the filter chain.
            }
        }

        filterChain.doFilter(request, response);
    }
}
