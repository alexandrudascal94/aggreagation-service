package com.tnt.aggregationservice.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ShipmentResult {

    @Getter
    private ConcurrentSkipListSet<String> keyToMatch = new ConcurrentSkipListSet<>();

    @Getter
    private HashMap<String, List<String>> resultMap = new HashMap<>();

    public void registerForMatch(List<String> track) {
        keyToMatch.addAll(track);
    }

    public boolean isCompleted(){
        return keyToMatch.isEmpty();
    }

    public void put(String key, List<String> value){
        this.resultMap.put(key, value);
        keyToMatch.remove(key);
    }
}
