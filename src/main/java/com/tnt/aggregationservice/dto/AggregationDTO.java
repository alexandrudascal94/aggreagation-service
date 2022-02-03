package com.tnt.aggregationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AggregationDTO {
    private Map<String, Double> pricing;
    private Map<String, String> track;
    private Map<String, List<String>> shipments;
}
