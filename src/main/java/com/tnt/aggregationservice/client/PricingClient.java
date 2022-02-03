package com.tnt.aggregationservice.client;

import com.tnt.aggregationservice.config.ClientConfig;
import com.tnt.aggregationservice.util.ErrorHandling;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingClient {
    public static final String Q_PARAM = "q";
    private final ClientConfig config;
    private final WebClient webClient;

    public Mono<Map> fetch(List<String> prices) {
        if (prices.isEmpty()) {
            return Mono.just(new HashMap());
        }

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(config.getPricingUrl())
                        .queryParam(Q_PARAM, prices)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, ErrorHandling::errorHandler)
                .bodyToMono(Map.class)
                .onErrorMap(ErrorHandling::mapError)
                .onErrorReturn(returnOnError(prices));
    }

    private Map returnOnError(List<String> keys){
        var result = new HashMap<String, Object>();
        keys.forEach(k ->  result.put(k, null));
        return result;
    }
}

