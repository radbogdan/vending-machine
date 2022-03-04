package com.mvp.vendingmachine.config;

import com.mvp.vendingmachine.security.authentication.LoginProvider;
import com.mvp.vendingmachine.security.authorization.JwtTokenAuthorization;
import com.mvp.vendingmachine.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityHeaders securityHeaders;
    @Autowired
    private RolesProperties rolesProperties;
    @Autowired
    private LoginProvider loginProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            // make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // handle an authorized attempts
            .exceptionHandling()
            .authenticationEntryPoint((req, rsp, e) ->
                rsp.sendError(HttpStatus.UNAUTHORIZED.value(), Encode.forHtml(HttpStatus.UNAUTHORIZED.getReasonPhrase())))
            .and()
            .authorizeRequests()
            // do not require authentication for following urls: /login
            .antMatchers(HttpMethod.POST, securityHeaders.getLoginUrl()).permitAll()
            .antMatchers(HttpMethod.POST, "/user").permitAll()
            .antMatchers(HttpMethod.GET, "/product/**").permitAll()
            .and()
            .authorizeRequests()
            // require authentication for the protected API Endpoints
            .antMatchers(securityHeaders.getEntryPointUrl()).authenticated()
            .and()            // Add a filters to authorize user credentials and add token in the response
            .addFilterBefore(new JwtUsernameAndPasswordAuthenticationFilter(new ProviderManager(loginProvider), securityHeaders, "vending-machine"), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new JwtTokenAuthorization(securityHeaders, rolesProperties), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}