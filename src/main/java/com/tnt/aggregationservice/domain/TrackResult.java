package com.tnt.aggregationservice.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class TrackResult {

    @Getter
    private ConcurrentSkipListSet<String> keysToMatch = new ConcurrentSkipListSet<>();

    @Getter
    private HashMap<String, String> resultMap = new HashMap<>();

    public void registerForMatch(List<String> track) {
        keysToMatch.addAll(track);
    }

    public boolean isCompleted(){
        return keysToMatch.isEmpty();
    }

    public void put(String key, String value){
        this.resultMap.put(key, value);
        keysToMatch.remove(key);
    }
}
