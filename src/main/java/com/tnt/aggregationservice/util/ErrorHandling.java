package com.tnt.aggregationservice.util;

import com.tnt.aggregationservice.exception.AggregationServiceException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class ErrorHandling {
    public static  Mono<? extends Throwable> errorHandler(ClientResponse clientResponse) {
        return Mono.error(new AggregationServiceException("Fetching failed with status code" + clientResponse.statusCode()));
    }

    public static Throwable mapError(Throwable e) {
        return new AggregationServiceException(" Fetching failed", e);
    }
}
