package com.tnt.aggregationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientConfig {

    private String shipmentUrl;
    private String trackUrl;
    private String pricingUrl;
    private String baseUrl;
    private Integer connectionTimeout;
    private Integer responseTimeout;
    private Integer throttledTimeout;
    private Integer bulkSize;
}
