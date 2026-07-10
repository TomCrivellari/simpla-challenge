package br.com.simplameta.ai_service.client;

import br.com.simplameta.ai_service.client.dto.MetaGoalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "meta-service",
        url = "${services.meta.url}"
)
public interface MetaServiceClient {

    @GetMapping("/api/v1/goals")
    List<MetaGoalResponse> findGoals(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );
}
