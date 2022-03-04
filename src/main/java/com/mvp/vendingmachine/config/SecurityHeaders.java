package com.mvp.vendingmachine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties
@lombok.Data
public class SecurityHeaders {

    @Value("${security.jwt.uri}")
    private String loginUrl;

    @Value("${security.jwt.expirationTime}")
    private int expirationTime;

    @Value("${security.jwt.secret}")
    private String secret;


    @Value("${security.jwt.entry-point-url}")
    private String entryPointUrl;

    @Value("${security.jwt.bearer-header}")
    private String bearerHeader;
}
