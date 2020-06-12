package com.auth0.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Needed to perform SSO logout with Auth0
 */
@Controller
public class LogoutHandler extends SecurityContextLogoutHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final String issuer;

    public LogoutHandler(ClientRegistrationRepository clientRegistrationRepository,
                         @Value("${spring.security.oauth2.client.provider.auth0.issuer-uri}") String issuer) {

        this.clientRegistrationRepository = clientRegistrationRepository;
        this.issuer = issuer;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       Authentication authentication) {

        // Invalidate the session and clear the security context
        super.logout(httpServletRequest, httpServletResponse, authentication);

        // Log user out of Auth0
        String returnTo = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String logoutUrl = String.format(
                "%sv2/logout?client_id=%s&returnTo=%s",
                this.issuer,
                getClientRegistration().getClientId(),
                returnTo
        );

        log.info("Will attempt to redirect to logout URL: {}", logoutUrl);
        try {
            httpServletResponse.sendRedirect(logoutUrl);
        } catch (IOException ioe) {
            log.error("Error redirecting to logout URL", ioe);
        }
    }

    private ClientRegistration getClientRegistration() {
        return this.clientRegistrationRepository.findByRegistrationId("auth0");
    }
}
