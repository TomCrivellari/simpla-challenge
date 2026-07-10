package br.com.simplameta.finance_service.config;

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
    public OpenAPI financeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simpla Meta Finance Service API")
                        .description("""
                                Finance service for dashboard balance, incomes and expenses.
                                All business endpoints require a Bearer JWT issued by auth-service.
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
