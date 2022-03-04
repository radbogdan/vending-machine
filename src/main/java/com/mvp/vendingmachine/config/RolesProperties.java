package com.mvp.vendingmachine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "security")
@Configuration
@EnableConfigurationProperties
@lombok.Data
public class RolesProperties {

    private Set<String> roles = new HashSet<>();

}