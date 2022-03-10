package com.mvp.vendingmachine.security.authentication;

import com.mvp.vendingmachine.storage.MongoUserRepository;
import com.mvp.vendingmachine.storage.model.StoredUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.lang.String.format;

@Component
public final class LoginProvider implements AuthenticationProvider {

    @Autowired
    private MongoUserRepository repository;


    @Override
    public Authentication authenticate(Authentication authentication) {
        final String username = (String) authentication.getPrincipal();
        final String password = (String) authentication.getCredentials();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new AuthenticationCredentialsNotFoundException("Username or Password is missing");
        }

        final StoredUser storedUser = repository.findById(username)
            .orElseThrow(() -> {
                return new AuthenticationException(format("User [%s] not found!", username)) {
                };
            });
        Set<SimpleGrantedAuthority> grantedAuthorities = Set.of(new SimpleGrantedAuthority(storedUser.getRole()));

        AbstractAuthenticationToken userAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
        userAuthenticationToken.setDetails(storedUser.getUsername());
        return userAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
