package com.it.template.api.router;

import com.it.template.api.handler.AppVersionHandler;
import com.it.template.api.handler.function.ApiHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@RequiredArgsConstructor
public class AppVersionRouter {

    private final ApiHandlerFilter apiHandlerFilter;

    @Bean
    public RouterFunction<ServerResponse> routeVersionInfo(AppVersionHandler handler) {
        return RouterFunctions.route(GET("/AppVersion"), handler::handleRouteVersionInfo).filter(apiHandlerFilter);
    }
}
