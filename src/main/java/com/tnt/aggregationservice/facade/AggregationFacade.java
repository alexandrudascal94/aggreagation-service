package com.tnt.aggregationservice.facade;

import com.tnt.aggregationservice.dto.AggregationDTO;
import com.tnt.aggregationservice.domain.PricingResult;
import com.tnt.aggregationservice.domain.ShipmentResult;
import com.tnt.aggregationservice.domain.TrackResult;
import com.tnt.aggregationservice.service.PricingService;
import com.tnt.aggregationservice.service.ShipmentService;
import com.tnt.aggregationservice.service.TrackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;

import java.util.List;

@Service
@AllArgsConstructor
public class AggregationFacade {

    private final PricingService priceService;
    private final TrackService trackService;
    private final ShipmentService shipmentService;

    public Mono<AggregationDTO> aggregate(List<String> prices, List<String> tracks, List<String> shipments)  {

        Mono<PricingResult> pricingMono = Mono.defer(() -> Mono.just(priceService.getPrices(prices)))
                .subscribeOn(Schedulers.boundedElastic());
        Mono<TrackResult> trackMono = Mono.defer(() -> Mono.just(trackService.getTracks(tracks)))
                .subscribeOn(Schedulers.boundedElastic());
        Mono<ShipmentResult> shipmentMono = Mono.defer(() -> Mono.just(shipmentService.getShipment(shipments)))
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(pricingMono, trackMono, shipmentMono)
                .map(t -> mapFetchResults(t));
    }

    private AggregationDTO mapFetchResults(Tuple3< PricingResult, TrackResult, ShipmentResult> results) {
        return AggregationDTO
                .builder()
                .pricing(results.getT1().getResultMap())
                .track(results.getT2().getResultMap())
                .shipments(results.getT3().getResultMap())
                .build();
    }
}
