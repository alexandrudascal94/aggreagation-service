package com.tnt.aggregationservice.controller;

import com.tnt.aggregationservice.dto.AggregationDTO;
import com.tnt.aggregationservice.facade.AggregationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AggregationController {

    private final AggregationFacade aggregationService;

    @GetMapping(path = "/aggregation")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AggregationDTO> getAggregation(@RequestParam("pricing") Optional<List<String>> pricing,
                                               @RequestParam("track") Optional<List<String>> track,
                                               @RequestParam("shipments") Optional<List<String>> shipments){

        return aggregationService.aggregate(
                        pricing.orElse(List.of()),
                        track.orElse(List.of()),
                        shipments.orElse(List.of()));
    }
}
