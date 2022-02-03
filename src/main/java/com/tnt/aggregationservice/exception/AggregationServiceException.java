package com.tnt.aggregationservice.exception;

public class AggregationServiceException extends RuntimeException {

    public AggregationServiceException(String message, Throwable exception) {
        super(message, exception);
    }

    public AggregationServiceException(String message) {
        super(message);
    }
}
