package com.mvp.vendingmachine.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvp.vendingmachine.config.SecurityHeaders;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.lang.String.format;

@Slf4j
public final class JwtUsernameAndPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private final AuthenticationManager authenticationManager;
    private final SecurityHeaders securityHeaders;
    private final String serviceName;
    private final static String ROLE_VALUE = "role";
    private final static String USERNAME = "username";

    public JwtUsernameAndPasswordAuthenticationFilter(
        final AuthenticationManager authenticationManager,
        final SecurityHeaders securityHeaders,
        final String serviceName
    ) {
        super(securityHeaders.getLoginUrl());
        this.authenticationManager = authenticationManager;
        this.securityHeaders = securityHeaders;
        this.serviceName = serviceName;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        try {
            final LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            if (StringUtils.isBlank(loginRequest.getUsername()) || StringUtils.isBlank(loginRequest.getPassword())) {
                log.warn("Username or Password not provided!");
                throw new AuthenticationServiceException("Username or Password not provided!");
            }

            final AbstractAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            return authenticationManager.authenticate(token);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Error occurred attempting authentication!", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException {
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime expirationTime = currentTime.plusHours(securityHeaders.getExpirationTime());
        final String username = String.valueOf(auth.getDetails());

        final String token = Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(serviceName)
            .setSubject(auth.getName())
            .claim(USERNAME, username)
            .claim(ROLE_VALUE, auth.getAuthorities())
            .setIssuedAt(new Date())
            .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(SignatureAlgorithm.HS256, securityHeaders.getSecret())
            .compact();

        response.getWriter()
            .write(new ObjectMapper().writeValueAsString(new TokenResponse(token)));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        log.info(format("Token for user [%s] created successfully!", auth.getName()));
    }


    private static class LoginRequest {

        private String username;
        private String password;

        private LoginRequest() {
        }

        public String getUsername() {
            return this.username;
        }


        public String getPassword() {
            return this.password;
        }

    }

    private static class TokenResponse {

        private String token;

        private TokenResponse(final String token) {
            this.token = token;
        }

        public String getToken() {
            return this.token;
        }
    }
}
