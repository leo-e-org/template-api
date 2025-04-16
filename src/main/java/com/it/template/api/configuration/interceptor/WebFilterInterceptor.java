package com.it.template.api.configuration.interceptor;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Component
public class WebFilterInterceptor implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (getAllowedEndpoints().stream().anyMatch(endpoint -> exchange.getRequest().getPath().value().contains(endpoint)))
            return chain.filter(exchange);

        log.info("Received HTTP {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath().pathWithinApplication().value());

        log.debug("Request origin: {} - Authorization {}",
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress(),
                exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        Long startTime = System.currentTimeMillis();
        return chain.filter(exchange).doFinally(signal -> {
            Long totalTime = System.currentTimeMillis() - startTime;
            log.info(" HTTP {} {} finished in {}ms",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath().pathWithinApplication().value(),
                    totalTime);
        });
    }

    protected List<String> getAllowedEndpoints() {
        return List.of("actuator", "api-docs", "configuration", "swagger-ui", "v3", "webjars");
    }
}
