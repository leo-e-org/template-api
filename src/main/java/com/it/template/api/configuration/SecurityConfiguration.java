package com.it.template.api.configuration;

import com.it.template.api.configuration.component.NotAuthenticatedEntryPoint;
import com.it.template.api.configuration.component.SecurityAuthenticationConverter;
import com.it.template.api.configuration.component.SecurityAuthenticationManager;
import com.it.template.api.configuration.handler.AccessDeniedHandler;
import com.it.template.api.evaluator.ApiPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${app.config.security.cors.enabled:false}")
    private boolean enableCors;
    @Value("#{'${app.config.security.cors.origins:}'.split(',')}")
    private List<String> allowedOrigins;
    @Value("#{'${app.config.security.cors.methods:}'.split(',')}")
    private List<String> allowedMethods;
    @Value("#{'${app.config.security.cors.headers:}'.split(',')}")
    private List<String> allowedHeaders;

    private final ApiPermissionEvaluator apiPermissionEvaluator;
    private final SecurityAuthenticationConverter securityAuthenticationConverter;
    private final SecurityAuthenticationManager securityAuthenticationManager;

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(securityAuthenticationManager);
        filter.setServerAuthenticationConverter(securityAuthenticationConverter);

        return http
                .cors(corsSpec -> {
                    if (enableCors) corsSpec.configurationSource(corsConfigurationSource());
                    else corsSpec.disable();
                })
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .headers(headerSpec -> headerSpec.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))

                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(new NotAuthenticatedEntryPoint())
                        .accessDeniedHandler(new AccessDeniedHandler()))

                .authenticationManager(securityAuthenticationManager)
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)

                .securityMatcher(new NegatedServerWebExchangeMatcher(
                        ServerWebExchangeMatchers.pathMatchers(getAllowedEndpoints().toArray(String[]::new))))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated())

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        allowedOrigins.forEach(configuration::addAllowedOrigin);
        allowedMethods.forEach(configuration::addAllowedMethod);
        allowedHeaders.forEach(configuration::addAllowedHeader);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(apiPermissionEvaluator);
        return expressionHandler;
    }

    protected List<String> getAllowedEndpoints() {
        return List.of("/actuator/**", "/configuration/**", "/configuration/ui", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/**");
    }
}
