package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentsQueue extends AbstractRequestQueue<List<String>> {
    public ShipmentsQueue(ClientConfig config) {
        super(config);
    }
}
