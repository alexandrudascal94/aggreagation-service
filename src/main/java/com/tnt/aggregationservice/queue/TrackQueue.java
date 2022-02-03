package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfig;
import org.springframework.stereotype.Service;

@Service
public class TrackQueue extends AbstractRequestQueue<String> {
    public TrackQueue(ClientConfig config) {
        super(config);
    }
}
