package com.auth0.example;

import reactor.core.publisher.Mono;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This filter is required for the sample to work on localhost. This class is copied from the
 * <a href="https://github.com/spring-projects/spring-security-samples/blob/main/reactive/webflux/java/oauth2/login/src/main/java/example/LoopbackIpRedirectWebFilter.java">WebFlux OAuth2 login sample app</a>.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedirectWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String host = exchange.getRequest().getURI().getHost();
        if (host != null && (host.equals("localhost") || host.equals("[0:0:0:0:0:0:0:1]"))) {
            UriComponents uri = UriComponentsBuilder.fromHttpRequest(exchange.getRequest()).host("127.0.0.1").build();
            exchange.getResponse().setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            exchange.getResponse().getHeaders().setLocation(uri.toUri());
            return Mono.empty();
        }
        return chain.filter(exchange);
    }

}
