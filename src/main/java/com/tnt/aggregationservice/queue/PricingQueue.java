package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfig;
import org.springframework.stereotype.Service;

@Service
public class PricingQueue extends AbstractRequestQueue<Double> {
    public PricingQueue(ClientConfig config) {
        super(config);
    }
}
