package com.it.template.api.handler.function;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ApiHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return request.method() == HttpMethod.OPTIONS
                ? ServerResponse.ok().body(BodyInserters.empty())
                : next.handle(request);
    }
}
