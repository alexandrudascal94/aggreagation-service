package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AbstractRequestQueue<ResponseType extends Object> {

    @Getter
    private final ConcurrentLinkedQueue<String> requestQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final HashMap<String, ResponseType> responseMap = new HashMap<>();
    private final ClientConfig config;

    public void registerRequests(List<String> keys) {
        var newKeys = keys.stream()
                .filter(k -> !requestQueue.contains(k))
                .collect(Collectors.toList());
        requestQueue.addAll(newKeys);
    }

    public boolean hasEnoughElementsToRequest() {
        return requestQueue.size() >= config.getBulkSize();
    }

    public synchronized List<String> drainQueue(int count) {
        List<String> result = new ArrayList<>();
        while (!requestQueue.isEmpty() && count != 0) {
            result.add(requestQueue.poll());
            count--;
        }
        return result;
    }

    public void registerResponse(Map<String, ResponseType> responseMap) {
        this.responseMap.putAll(responseMap);
    }

    public boolean hasResponseFor(String priceKey) {
        return responseMap.containsKey(priceKey);
    }

    public ResponseType tryMatchKey(String priceKey) {
        return this.responseMap.get(priceKey);
    }

    public void scheduleToCleanResponseFor(String key) {
        Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> responseMap.remove(key), 600, TimeUnit.MILLISECONDS);
    }
}
