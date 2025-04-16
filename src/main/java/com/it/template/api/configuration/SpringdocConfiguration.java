package com.it.template.api.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfiguration {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(getInfo());
        // .components(getComponents())
        // .addSecurityItem(getSecurityItem());
    }

    private Info getInfo() {
        return new Info().title("Template API").description("Template API definition").version("1.0.0");
    }

    // private Components getComponents() {
    //     return new Components()
    //             .addSecuritySchemes("Bearer Token", new SecurityScheme()
    //                     .type(SecurityScheme.Type.HTTP)
    //                     .scheme("bearer"));
    // }

    // private SecurityRequirement getSecurityItem() {
    //     return new SecurityRequirement().addList("Bearer Token");
    // }
}
