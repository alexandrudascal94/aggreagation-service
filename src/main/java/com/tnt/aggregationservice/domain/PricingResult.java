package com.tnt.aggregationservice.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class PricingResult {

    @Getter
    private ConcurrentSkipListSet<String> keysToMatch = new ConcurrentSkipListSet<>();

    @Getter
    private HashMap<String, Double> resultMap = new HashMap<>();

    public void registerForMatch(List<String> keys) {
        keysToMatch.addAll(keys);
    }

    public boolean isCompleted(){
        return keysToMatch.isEmpty();
    }

    public void put(String key, Double value){
        this.resultMap.put(key, value);
        keysToMatch.remove(key);
    }
}
