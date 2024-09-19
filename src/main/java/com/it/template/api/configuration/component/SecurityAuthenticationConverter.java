package com.it.template.api.configuration.component;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class SecurityAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer ")) {
            log.warn(new StringMapMessage()
                    .with("message", "No Bearer Token found in request headers")
                    .with("app.request_headers", exchange.getRequest().getHeaders()));

            return Mono.empty();
        }

        return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
    }
}
