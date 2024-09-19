package com.it.template.api.client;

import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

@Log4j2
@Component
@RequiredArgsConstructor
public class LegacyClient extends HelperClient {

    private final WebClient apiLegacyWebClient;

    public Object getAppVersion() {
        return apiLegacyWebClient
                .get()
                .uri("/AppVersion")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> statusResponse(response, "API Legacy"))
                .toEntity(new ParameterizedTypeReference<>(){})
                .mapNotNull(ResponseEntity::getBody)
                .onErrorMap(ReadTimeoutException.class, e -> errorMap("API Legacy", "getAppVersion"))
                .onErrorResume(this::errorResume)
                .subscribeOn(Schedulers.boundedElastic())
                .share()
                .block();
    }
}
