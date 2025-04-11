package com.it.template.api.exception.handler;

import com.it.template.api.model.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.IntStream;

@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final Integer HTTP_4XX_RANGE_START = 400;
    private static final Integer HTTP_4XX_RANGE_END = 499;

    WebExceptionHandler(final ErrorAttributes errorAttributes,
                        final ApplicationContext applicationContext,
                        final ServerCodecConfigurer configurer) {

        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageReaders(configurer.getReaders());
        super.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        return switch (getError(request)) {
            case AccessDeniedException e -> serverResponse(new ApiException(HttpStatus.FORBIDDEN));
            case ResponseStatusException e -> serverResponse(new ApiException(HttpStatus.valueOf(e.getStatusCode().value())));
            case ApiException e -> serverResponse(e);
            case WebClientResponseException e -> serverResponse(new ApiException(e.getStatusText()));
            default -> serverResponse(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, getError(request).getClass().getSimpleName()));
        };
    }

    private Mono<ServerResponse> serverResponse(ApiException exception) {
        if (IntStream.rangeClosed(HTTP_4XX_RANGE_START, HTTP_4XX_RANGE_END)
                .anyMatch(n -> Objects.nonNull(exception.getCode()) && n == exception.getCode()))
            log.warn(new StringMapMessage()
                    .with("message", "Web Exception Handler - response error")
                    .with("app.error_code", exception.getCode())
                    .with("error.message", exception.getExceptionMessage())
                    .with("error.stack_trace", exception.getCause()));
        else
            log.error(new StringMapMessage()
                    .with("message", "Web Exception Handler - response error")
                    .with("app.error_code", exception.getCode())
                    .with("error.message", exception.getExceptionMessage())
                    .with("error.stack_trace", exception.getCause()));

        return ServerResponse
                .status(Objects.nonNull(exception.getCode())
                        ? HttpStatus.valueOf(exception.getCode())
                        : HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(exception.toString()));
    }
}
