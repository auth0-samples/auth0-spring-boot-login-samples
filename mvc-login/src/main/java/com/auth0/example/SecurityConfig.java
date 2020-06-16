package com.auth0.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LogoutHandler logoutHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(LogoutHandler logoutHandler, ClientRegistrationRepository clientRegistrationRepository) {
        this.logoutHandler = logoutHandler;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .mvcMatchers("/", "/images/**").permitAll()
               .anyRequest().authenticated()
           .and().oauth2Login()
               .authorizationEndpoint()
               .authorizationRequestResolver(new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")).and()
           .and().logout()
               .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
               .addLogoutHandler(logoutHandler);
    }
}
