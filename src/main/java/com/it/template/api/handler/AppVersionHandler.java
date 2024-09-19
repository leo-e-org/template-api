package com.it.template.api.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AppVersionHandler extends MainHandler {

    public Mono<ServerResponse> handleRouteVersionInfo(ServerRequest request) {
        return ok()
                .headers(defaultHeaders())
                .bodyValue(Objects.toString(this.getClass().getPackage().getImplementationVersion(), "local"));
    }
}
