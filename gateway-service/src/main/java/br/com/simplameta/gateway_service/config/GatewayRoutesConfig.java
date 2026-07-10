package br.com.simplameta.gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> authRoutes(
            @Value("${services.auth.url}") String authServiceUrl
    ) {
        return route("auth-service")
                .route(path("/api/v1/auth/**"), http())
                .before(uri(authServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> financeRoutes(
            @Value("${services.finance.url}") String financeServiceUrl
    ) {
        return route("finance-service")
                .route(path(
                        "/api/v1/dashboard",
                        "/api/v1/transactions",
                        "/api/v1/transactions/**"
                ), http())
                .before(uri(financeServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> metaRoutes(
            @Value("${services.meta.url}") String metaServiceUrl
    ) {
        return route("meta-service")
                .route(path(
                        "/api/v1/goals",
                        "/api/v1/goals/**"
                ), http())
                .before(uri(metaServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authSwaggerRoute(
            @Value("${services.auth.url}") String authServiceUrl
    ) {
        return route("auth-swagger-docs")
                .route(path("/swagger/auth/api-docs"), http())
                .before(uri(authServiceUrl))
                .before(rewritePath("/swagger/auth/api-docs", "/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> financeSwaggerRoute(
            @Value("${services.finance.url}") String financeServiceUrl
    ) {
        return route("finance-swagger-docs")
                .route(path("/swagger/finance/api-docs"), http())
                .before(uri(financeServiceUrl))
                .before(rewritePath("/swagger/finance/api-docs", "/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> metaSwaggerRoute(
            @Value("${services.meta.url}") String metaServiceUrl
    ) {
        return route("meta-swagger-docs")
                .route(path("/swagger/meta/api-docs"), http())
                .before(uri(metaServiceUrl))
                .before(rewritePath("/swagger/meta/api-docs", "/api-docs"))
                .build();
    }
}
