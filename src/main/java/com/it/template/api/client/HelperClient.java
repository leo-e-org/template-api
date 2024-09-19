package com.it.template.api.client;

import com.it.template.api.model.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Log4j2
public abstract class HelperClient {

    Mono<? extends Throwable> statusResponse(ClientResponse response, String clientName) {
        ApiException e = new ApiException(HttpStatus.valueOf(response.statusCode().value()));

        if (response.statusCode().is4xxClientError())
            log.warn(new StringMapMessage()
                    .with("message", String.format("%s Client - 4xx error", clientName))
                    .with("error_code", response.statusCode().value())
                    .with("error_message", HttpStatus.valueOf(response.statusCode().value()).getReasonPhrase())
                    .with("error.type", e.getClass().getName())
                    .with("error.stack_trace", e));
        else
            log.error(new StringMapMessage()
                    .with("message", String.format("%s Client - 5xx error", clientName))
                    .with("error_code", response.statusCode().value())
                    .with("error_message", HttpStatus.valueOf(response.statusCode().value()).getReasonPhrase())
                    .with("error.type", e.getClass().getName())
                    .with("error.stack_trace", e));

        return Mono.error(e);
    }

    ApiException errorMap(String clientName, String methodName) {
        return new ApiException(HttpStatus.GATEWAY_TIMEOUT,
                String.format("%s Client - %s timed out waiting for remote client response", clientName, methodName));
    }

    <T> Mono<T> errorResume(Throwable t) {
        return t instanceof ApiException
                ? Mono.error(t)
                : Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, t.getMessage()));
    }

    Throwable retryExhaustedThrow(Retry.RetrySignal signal, String clientName, String methodName) {
        log.error(new StringMapMessage()
                .with("message", String.format("%s Client - %s failed after max retries", clientName, methodName))
                .with("error.stack_trace", signal.failure()));

        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("%s Client - %s failed after max retries", clientName, methodName));
    }
}
