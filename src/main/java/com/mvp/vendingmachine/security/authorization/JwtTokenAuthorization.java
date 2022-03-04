package com.mvp.vendingmachine.security.authorization;

import com.mvp.vendingmachine.config.RolesProperties;
import com.mvp.vendingmachine.config.SecurityHeaders;
import com.mvp.vendingmachine.security.authorization.exception.AuthorizationException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
public final class JwtTokenAuthorization extends OncePerRequestFilter {


    private static final String ROLE_VALUE = "role";
    public static final String BEARER_VALUE = "Bearer ";
    private final SecurityHeaders securityHeaders;
    private final RolesProperties rolesProperties;

    public JwtTokenAuthorization(
        final SecurityHeaders securityHeaders, final RolesProperties rolesProperties) {
        this.securityHeaders = securityHeaders;
        this.rolesProperties = rolesProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(token) || !token.startsWith(BEARER_VALUE)) {
            filterChain.doFilter(request, response);
            return;
        }
        final Claims claims = parseClaims(token).getBody();
        final String username = claims.getSubject();
        try {
            if (username != null) {
                final boolean authorized = isAuthorized(claims);
                if (!authorized) {
                    log.warn("Unauthorized to perform the request");
                    throw new AuthorizationException("Unauthorized to perform the request");
                }
                log.debug(format("User [%s] authenticated successfully!", username));
                Set<SimpleGrantedAuthority> authorities = claims.get(ROLE_VALUE, (Class<List<LinkedHashMap<String, String>>>) (Class<?>) List.class)
                    .stream().flatMap(m -> m.values().stream())
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

                final AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } else {
                log.error("Missing username from token");
                throw new AuthorizationException("Missing username from token");
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error(format("Authorization for user [%s] failed!", username), ex);
            // In case of failure. Make sure it's clear; so guarantee user won't be authenticated
            SecurityContextHolder.clearContext();
            throw ex;
        }
    }

    private boolean isAuthorized(final Claims claims) {
        Optional<SimpleGrantedAuthority> roleClaim = claims.get(ROLE_VALUE, (Class<List<LinkedHashMap<String, String>>>) (Class<?>) List.class)
            .stream().flatMap(m -> m.values().stream())
            .map(SimpleGrantedAuthority::new).findFirst();
        if (roleClaim.isPresent()) {
            return rolesProperties.getRoles().contains(roleClaim.get().getAuthority());
        }
        return false;
    }

    private Jws<Claims> parseClaims(final String token) {
        try {
            return Jwts.parser()
                .setSigningKey(securityHeaders.getSecret())
                .parseClaimsJws(token.replace(securityHeaders.getBearerHeader(), ""));
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            log.error(format("Invalid JWT token: %s", ex.getMessage()));
            throw new AuthorizationException("Invalid JWT token", ex);
        }
    }
}
