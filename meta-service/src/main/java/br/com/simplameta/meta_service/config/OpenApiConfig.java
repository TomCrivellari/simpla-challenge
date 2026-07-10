package br.com.simplameta.meta_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI metaServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simpla Meta Goal Service API")
                        .description("""
                                Goal service for financial goals, contributions,
                                monthly saving projections and progress tracking.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("Simpla Meta"))
                        .license(new License()
                                .name("Private project")))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
