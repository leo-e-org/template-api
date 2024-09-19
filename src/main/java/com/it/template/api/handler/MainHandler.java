package com.it.template.api.handler;

import com.it.template.api.model.exception.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

public abstract class MainHandler {

    Consumer<HttpHeaders> optionHeaders() {
        return headers -> {
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Accept");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, PUT, POST, DELETE, PATCH, HEAD, OPTIONS");
        };
    }

    Consumer<HttpHeaders> defaultHeaders() {
        return headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name());
        };
    }

    Function<ServerResponse, Mono<? extends ServerResponse>> mapResponse() {
        return response -> {
            if (response.statusCode().isError())
                return Mono.error(new ApiException(HttpStatus.valueOf(response.statusCode().value())));

            return Mono.just(response);
        };
    }

    Function<Throwable, Mono<? extends ServerResponse>> mapErrorResume() {
        return e -> Mono.just(String.format("Error: %s", e.getMessage()))
                .flatMap(s -> badRequest().contentType(MediaType.TEXT_PLAIN).bodyValue(s));
    }

    Mono<ServerResponse> mapNoContent() {
        return noContent().build();
    }

    Mono<ServerResponse> mapBadRequest() {
        return badRequest().bodyValue(new ApiException(HttpStatus.BAD_REQUEST));
    }

    Mono<ServerResponse> mapUnauthorized() {
        return status(HttpStatus.UNAUTHORIZED).bodyValue(new ApiException(HttpStatus.UNAUTHORIZED));
    }

    Mono<ServerResponse> mapNotFound() {
        return status(HttpStatus.NOT_FOUND).bodyValue(new ApiException(HttpStatus.NOT_FOUND));
    }

    Mono<ServerResponse> mapGone() {
        return status(HttpStatus.GONE).bodyValue(new ApiException(HttpStatus.GONE));
    }

    Mono<ServerResponse> mapInternalServerError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
