package com.tnt.aggregationservice.service;

import com.tnt.aggregationservice.client.ShipmentClient;
import com.tnt.aggregationservice.config.ClientConfig;
import com.tnt.aggregationservice.domain.ShipmentResult;
import com.tnt.aggregationservice.exception.AggregationServiceException;
import com.tnt.aggregationservice.queue.ShipmentsQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentClient shipmentClient;
    private final ClientConfig config;
    private final ShipmentsQueue requestQueue;

    public ShipmentResult getShipment(List<String> keys) {
        registerRequests(keys);
        return subscribeOnResponses(keys);
    }

    public void registerRequests(List<String> keys) {
        requestQueue.registerRequests(keys);
        tryToRequest();
    }

    public ShipmentResult subscribeOnResponses(List<String> forKeys) {

        scheduleTimeoutRequest(config.getThrottledTimeout());
        return tryMatchResponses(forKeys);
    }

    private synchronized void tryToRequest() {
        if (requestQueue.hasEnoughElementsToRequest()) {
            request(requestQueue.drainQueue(config.getBulkSize()));
        }
    }

    private void request(List<String> keysToProcess) {
        shipmentClient.fetch(keysToProcess).subscribe(responseMap -> requestQueue.registerResponse(responseMap));
    }

    private void scheduleTimeoutRequest(int throttledTimeout) {
        Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {
                    request(requestQueue.drainQueue(config.getBulkSize()));
                }, throttledTimeout, TimeUnit.MILLISECONDS);
    }

    private ShipmentResult tryMatchResponses(List<String> keys) {
        var result = new ShipmentResult();
        result.registerForMatch(keys);

        while (!result.isCompleted()) {
            tryMatchResult(result);
            delayCheck();
        }
        return result;
    }

    private void tryMatchResult(ShipmentResult result) {
        for (var key : result.getKeyToMatch()) {
            if (requestQueue.hasResponseFor(key)) {
                result.put(key, requestQueue.tryMatchKey(key));
                requestQueue.scheduleToCleanResponseFor(key);
            }
        }
    }

    private void delayCheck() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new AggregationServiceException("Delay response matching failure", e);
        }
    }
}
