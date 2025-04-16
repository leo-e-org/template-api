package com.it.template.api.router;

import com.it.template.api.handler.AppVersionHandler;
import com.it.template.api.handler.function.ApiHandlerFilter;
import com.it.template.api.model.AppVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@RequiredArgsConstructor
public class AppVersionRouter {

    private final ApiHandlerFilter apiHandlerFilter;

    @Bean
    @RouterOperation(operation =
    @Operation(operationId = "getAppVersion",
            summary = "Get App Version",
            responses = {
                    @ApiResponse(responseCode = "200", content =
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = AppVersion.class)))
            }))
    public RouterFunction<ServerResponse> routeVersionInfo(AppVersionHandler handler) {
        return RouterFunctions.route(GET("/app-version"), handler::handleRouteVersionInfo).filter(apiHandlerFilter);
    }
}
