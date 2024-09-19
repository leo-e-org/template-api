package com.it.template.api.configuration;

import com.it.template.api.model.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Primary
@Configuration
public class WebClientConfiguration {

    @Value("${app.api.legacy.url:}")
    private String apiLegacyUrl;

    @Value("${app.config.webclient.max-memory-size:20}")
    private Integer maxMemorySize;

    private static final Integer MEMORY_SIZE_MULTIPLIER = 1024;

    @Bean
    WebClient apiLegacyWebClient(ReactorClientHttpConnector connector) {
        return buildWebClient(apiLegacyUrl, connector);
    }

    private WebClient buildWebClient(String baseUrl, ReactorClientHttpConnector connector) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(connector)
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(maxMemorySize * MEMORY_SIZE_MULTIPLIER * MEMORY_SIZE_MULTIPLIER))
                        .build())
                .filter(ExchangeFilterFunction.ofResponseProcessor(WebClientConfiguration::exchangeFilterResponseProcessor))
                .build();
    }

    private static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
        if (response.statusCode().isError()) {
            ApiException e = new ApiException(HttpStatus.valueOf(response.statusCode().value()));

            if (response.statusCode().is4xxClientError())
                log.warn(new StringMapMessage()
                        .with("message", "Response Processor Filter - 4xx error")
                        .with("error_code", response.statusCode().value())
                        .with("error_message", HttpStatus.valueOf(response.statusCode().value()).getReasonPhrase())
                        .with("error.stack_trace", e));
            else if (response.statusCode().is5xxServerError())
                log.error(new StringMapMessage()
                        .with("message", "Response Processor Filter - 5xx error")
                        .with("error_code", response.statusCode().value())
                        .with("error_message", HttpStatus.valueOf(response.statusCode().value()).getReasonPhrase())
                        .with("error.stack_trace", e));

            return Mono.error(e);
        }

        return Mono.just(response);
    }
}
